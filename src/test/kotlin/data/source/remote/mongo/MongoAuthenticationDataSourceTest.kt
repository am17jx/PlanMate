package data.source.remote.mongo

import com.google.common.truth.Truth.assertThat
import com.mongodb.kotlin.client.coroutine.MongoCollection
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.example.data.mapper.toUser
import org.example.data.mapper.toUserDTO
import org.example.data.models.UserDTO
import org.example.data.source.remote.mongo.MongoAuthenticationDataSource
import org.example.logic.models.User
import org.example.logic.models.UserRole
import org.example.logic.utils.CreationItemFailedException
import org.example.logic.utils.GetItemsFailedException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@OptIn(ExperimentalCoroutinesApi::class)
class MongoAuthenticationDataSourceTest {
    private lateinit var mongoCollection: MongoCollection<UserDTO>
    private lateinit var remoteAuthenticationDataSource: MongoAuthenticationDataSource
    private val user = User("1", "test", "password", UserRole.USER)
    private val userDTO = user.toUserDTO()

    @BeforeEach
    fun setup() {
        mongoCollection = mockk(relaxed = true)
        remoteAuthenticationDataSource = MongoAuthenticationDataSource(mongoCollection)
    }

    @Test
    fun `saveUser should insert the user into the user collection when called`() = runTest {

        remoteAuthenticationDataSource.saveUser(user)

        coVerify(exactly = 1) { mongoCollection.insertOne(userDTO, any()) }
    }


    @Test
    fun `saveUser should CreationItemFailedException exception when insert in mongo fails`() = runTest {
        coEvery { mongoCollection.insertOne(userDTO, any()) } throws Exception()

        assertThrows<CreationItemFailedException> {
            remoteAuthenticationDataSource.saveUser(user)
        }
    }


    @Test
    fun `getAllUsers should return all users when get all from the user collection`() = runTest {
        remoteAuthenticationDataSource.getAllUsers()

        coVerify(exactly = 1) { mongoCollection.find(filter = any()) }
    }

    @Test
    fun `getAllUsers should throw GetItemsFailedException exception when get all users from mongo fails`() = runTest {
        coEvery { mongoCollection.find(filter = any()) } throws Exception()

        assertThrows<GetItemsFailedException> {
            remoteAuthenticationDataSource.getAllUsers()
        }
    }










}