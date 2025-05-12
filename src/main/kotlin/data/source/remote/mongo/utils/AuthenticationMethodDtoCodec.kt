package data.source.remote.mongo.utils

import org.bson.BsonReader
import org.bson.BsonWriter
import org.bson.codecs.Codec
import org.bson.codecs.DecoderContext
import org.bson.codecs.EncoderContext
import org.example.data.source.remote.models.UserDTO

class AuthenticationMethodDtoCodec : Codec<UserDTO.AuthenticationMethodDto> {
    override fun encode(writer: BsonWriter, value: UserDTO.AuthenticationMethodDto, encoderContext: EncoderContext) {
        writer.writeStartDocument()
        when (value) {
            is UserDTO.AuthenticationMethodDto.Password -> {
                writer.writeString("type", "password")
                writer.writeString("password", value.password)
            }
        }
        writer.writeEndDocument()
    }

    override fun decode(reader: BsonReader, decoderContext: DecoderContext): UserDTO.AuthenticationMethodDto {
        reader.readStartDocument()
        val type = reader.readString("type")
        val result = when (type) {
            "password" -> {
                val password = reader.readString("password")
                UserDTO.AuthenticationMethodDto.Password(password)
            }
            else -> throw IllegalArgumentException("Unknown auth method: $type")
        }
        reader.readEndDocument()
        return result
    }

    override fun getEncoderClass(): Class<UserDTO.AuthenticationMethodDto> =
        UserDTO.AuthenticationMethodDto::class.java
}
