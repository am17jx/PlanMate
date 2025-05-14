package data.source.remote.mongo

import com.google.common.truth.Truth.assertThat
import com.mongodb.client.model.Filters
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.InsertOneResult
import com.mongodb.client.result.UpdateResult
import com.mongodb.kotlin.client.coroutine.FindFlow
import com.mongodb.kotlin.client.coroutine.MongoCollection
import io.mockk.*
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.bson.BsonInt32
import org.example.data.source.remote.models.ProjectStateDTO
import org.example.data.source.remote.mongo.MongoProjectStateDataSource
import org.example.data.source.remote.mongo.utils.mapper.toState
import org.example.data.source.remote.mongo.utils.mapper.toStateDTO
import org.example.logic.models.ProjectState
import org.example.logic.utils.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class MongoProjectStateDataSourceTest {

    private lateinit var mongoCollection: MongoCollection<ProjectStateDTO>
    private lateinit var dataSource: MongoProjectStateDataSource
    private lateinit var testState: ProjectState
    private lateinit var testDto: ProjectStateDTO
    private val testId = Uuid.random()
    private val testProjectId = Uuid.random()

    @BeforeEach
    fun setUp() {
        mongoCollection = mockk(relaxed = true)
        dataSource = MongoProjectStateDataSource(mongoCollection)

        testState = ProjectState(
            id = testId,
            title = "To Do",
            projectId = testProjectId
        )
        testDto = testState.toStateDTO()
    }

    @Test
    fun `should throw TaskNotFoundException when get by id fails`() = runTest {
        coEvery { mongoCollection.find(Filters.eq("id", testId.toHexString())) } throws RuntimeException()

        assertThrows<TaskNotFoundException> {
            dataSource.getProjectStateById(testId)
        }
    }

    @Test
    fun `should throw TaskNotFoundException when get by project id fails`() = runTest {
        coEvery {
            mongoCollection.find(Filters.eq("projectId", testProjectId.toHexString()))
        } throws RuntimeException()

        assertThrows<TaskNotFoundException> {
            dataSource.getProjectStates(testProjectId)
        }
    }

    @Test
    fun `should return null when project state not found by id`() = runTest {
        coEvery {
            mongoCollection.find(Filters.eq("id", testId.toHexString())).firstOrNull()
        } returns null

        val result = dataSource.getProjectStateById(testId)

        assertThat(result).isNull()
    }

    @Test
    fun `should return empty list when no project states found by project id`() = runTest {
        coEvery {
            mongoCollection.find(Filters.eq("projectId", testProjectId.toHexString())).toList()
        } returns emptyList()

        val result = dataSource.getProjectStates(testProjectId)

        assertThat(result).isEmpty()
    }
}