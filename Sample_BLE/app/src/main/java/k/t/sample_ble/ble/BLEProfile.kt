package k.t.sample_ble.ble

import java.util.*

/*
A67844B7-4836-44E0-8AE1-E447EF9CB0CC
*/

object BLEProfile {
    const val EOT: Byte = 0x04

    val SERVICE_UUID: UUID = UUID.fromString("0000b81d-0000-1000-8000-00805f9b34fb")
    val CHARACTERISTIC_WRITE_UUID = UUID.fromString("72C26E6F-AA0D-413B-AA9E-5C1FE9354CB3")
    val CHARACTERISTIC_WRITE_NO_RESPONSE_UUID = UUID.fromString("D00AC097-F9C2-4D97-B322-C2FF468ECEF0")
}