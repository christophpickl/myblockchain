package com.github.christophpickl.myblockchain.server

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.kpotpourri.http4k.buildHttp4k
import com.github.christophpickl.kpotpourri.http4k.get
import com.github.christophpickl.myblockchain.common.encodeBase64
import com.github.christophpickl.myblockchain.common.toPrettyString
import com.google.common.base.MoreObjects
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.Arrays
import javax.servlet.http.HttpServletResponse

class Address(
        val name: String,
        val publicKey: ByteArray
) {
    val hash: ByteArray = calculateHash(name.toByteArray(), publicKey)

    override fun equals(other: Any?): Boolean {
        if (other !is Address) return false
        return Arrays.equals(this.hash, other.hash)
    }

    override fun hashCode() = Arrays.hashCode(hash)

    override fun toString() = MoreObjects.toStringHelper(this)
            .add("name", name)
            .add("publicKey", publicKey.toPrettyString())
            .toString()
}

@Service
class AddressService {

    private val log = LOG {}
    private val http4k = buildHttp4k {  }
    private val addresses = LinkedHashMap<String, Address>()

    fun byHash(hash: ByteArray) = addresses[hash.encodeBase64()]

    fun all() = addresses.values.toList()

    fun add(address: Address) {
        log.debug { "add(address=$address)" }
        addresses[address.hash.encodeBase64()] = address
    }

    fun synchronize(node: Node) {
        log.debug { "synchronize(node=$node)" }
        http4k.get<List<Address>>(node.address.toString() + "/address").forEach { add(it) }
    }
}

@RestController
@RequestMapping("address")
class AddressController @Autowired constructor(
        private val addressService: AddressService,
        private val nodeService: NodeService
) {

    private val log = LOG {}

    @RequestMapping
    fun getAddresses() = addressService.all()

    @RequestMapping(method = arrayOf(RequestMethod.PUT))
    fun addAddress(
            @RequestBody address: Address,
            @RequestParam(required = false, defaultValue = "false") publish: Boolean,
            response: HttpServletResponse
    ) {
        log.debug { "addAddress(address=$address, publish=$publish)" }
        val foundAddress = addressService.byHash(address.hash)
        if (foundAddress == null) {
            addressService.add(address)
            if (publish) {
                nodeService.broadcastPut("address", address);
            }
            response.status = HttpServletResponse.SC_ACCEPTED
        } else {
            response.status = HttpServletResponse.SC_NOT_ACCEPTABLE
        }
    }

}
