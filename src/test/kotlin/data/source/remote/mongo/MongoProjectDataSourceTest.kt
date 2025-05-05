package data.source.remote.mongo

import com.google.common.truth.Truth.assertThat
import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoCollection
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.example.data.mapper.toProjectDTO
import org.example.data.models.ProjectDTO
import org.example.data.source.remote.contract.RemoteProjectDataSource
import org.example.data.source.remote.mongo.MongoProjectDataSource
import org.example.logic.models.Project
import org.example.logic.models.State
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MongoProjectDataSourceTest {
    private lateinit var mongoClientCollection : MongoCollection<ProjectDTO>
    private lateinit var remoteProjectDataSource: RemoteProjectDataSource
    private lateinit var testProjects: List<Project>
    private lateinit var testProjectDTOs: List<ProjectDTO>


    @BeforeEach
   fun setUp() {
        mongoClientCollection = mockk(relaxed = true)
        testProjects = listOf(
            Project(
                id = "1",
                name = "Project 1",
                states = listOf(State(id = "1", title = "To Do")),
                auditLogsIds = listOf("100"),
            ),
            Project(
                id = "2",
                name = "Project 2",
                states = listOf(State(id = "2", title = "In Progress")),
                auditLogsIds = listOf("200"),
            ),
        )
        testProjectDTOs = testProjects.map { it.toProjectDTO() }
        remoteProjectDataSource = MongoProjectDataSource(mongoClientCollection)
    }


    @Test
    fun `should return list of project  when  try to get projects from MongoDB`() = runTest {
        remoteProjectDataSource.getAllProjects()
        advanceUntilIdle()
        coVerify(exactly = 1) { mongoClientCollection.find(filter = any()) }
    }

    @Test
    fun `should return project  when create project at MongoDB`() = runTest {
        val newProject = Project(id = "3", name = "Project 3", states = listOf(State(id = "3", title = "Done")), auditLogsIds = listOf("300"))
        val projectDTO = ProjectDTO(id = "3", name = "Project 3", states = listOf(State(id = "3", title = "Done")), auditLogsIds = listOf("300"))

        val createProject = remoteProjectDataSource.createProject(newProject)
        coVerify(exactly = 1) { mongoClientCollection.insertOne(projectDTO, any()) }
        advanceUntilIdle()
        assertThat(createProject).isEqualTo(newProject)
    }

    @Test
    fun `should return project when update project at MongoDB`() = runTest {
        val newProject = Project(id = "3", name = "Project 3", states = listOf(State(id = "3", title = "Done")), auditLogsIds = listOf("300"))
        val projectDTO = ProjectDTO(id = "3", name = "Project 3", states = listOf(State(id = "3", title = "Done")), auditLogsIds = listOf("300"))

        val createProject = remoteProjectDataSource.updateProject(newProject)
        coVerify(exactly = 1) { mongoClientCollection.replaceOne(Filters.eq("id", newProject.id),projectDTO, any()) }
        advanceUntilIdle()
        assertThat(createProject).isEqualTo(newProject)
    }


    @Test
    fun `should return project when get project by Id project at MongoDB`() = runTest {
         remoteProjectDataSource.getProjectById("1")
        coVerify(exactly = 1) { mongoClientCollection.find(filter = any())}
    }

    @Test
    fun `should delete project when delete project by Id at MongoDB`() = runTest {
         remoteProjectDataSource.deleteProject("1")
        coVerify(exactly = 1) { mongoClientCollection.deleteOne(filter = any(),options= any()) }
    }




}


