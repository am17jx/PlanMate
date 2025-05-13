package data.source.remote.mongo

import com.google.common.truth.Truth.assertThat
import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoCollection
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import mockdata.createProject
import org.example.data.repository.sources.remote.RemoteProjectDataSource
import org.example.data.source.remote.models.ProjectDTO
import org.example.data.source.remote.mongo.MongoProjectDataSource
import org.example.data.source.remote.mongo.utils.mapper.toProjectDTO
import org.example.data.utils.Constants.ID
import org.example.logic.models.Project
import org.example.logic.utils.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class MongoProjectDataSourceTest {
    private lateinit var mongoClientCollection: MongoCollection<ProjectDTO>
    private lateinit var remoteProjectDataSource: RemoteProjectDataSource
    private lateinit var testProjects: List<Project>
    private lateinit var testProjectDTOs: List<ProjectDTO>
    private val ids = List(6) { Uuid.random() }

    @BeforeEach
    fun setUp() {
        mongoClientCollection = mockk(relaxed = true)
        testProjects = listOf(
            createProject(
                id = ids[1],
                name = "Project 1",
            ),
            Project(
                id = ids[2], name = "Project 2"
            ),
        )
        testProjectDTOs = testProjects.map { it.toProjectDTO() }
        remoteProjectDataSource = MongoProjectDataSource(mongoClientCollection)
    }

    @Test
    fun `should return list of project  when  try to get projects from MongoDB`() = runTest {
        remoteProjectDataSource.getAllProjects()

        coVerify(exactly = 1) { mongoClientCollection.find(filter = any()) }
    }

    @Test
    fun `should throw NoProjectsFoundException exception when try to get projects fails in MongoDB`() = runTest {
        coEvery { mongoClientCollection.find(filter = any()) } throws NoProjectsFoundException()

        assertThrows<NoProjectsFoundException> { remoteProjectDataSource.getAllProjects() }
    }

    @Test
    fun `should return project when create project at MongoDB`() = runTest {
        val newProject = Project(
            id = ids[3],
            name = "Project 3",
        )
        val projectDTO = ProjectDTO(
            id = ids[3].toHexString(), name = "Project 3"
        )

        val createProject = remoteProjectDataSource.createProject(newProject)

        coVerify(exactly = 1) { mongoClientCollection.insertOne(projectDTO, any()) }

        assertThat(createProject).isEqualTo(newProject)
    }

    @Test
    fun `should throw ProjectCreationFailedException exception when create project fails in MongoDB`() = runTest {
        val newProject = Project(
            id = ids[3],
            name = "Project 3",
        )
        val projectDTO = ProjectDTO(
            id = ids[3].toHexString(), name = "Project 3"
        )

        coEvery { mongoClientCollection.insertOne(projectDTO, any()) } throws ProjectCreationFailedException()

        assertThrows<ProjectCreationFailedException> { remoteProjectDataSource.createProject(newProject) }
    }

    @Test
    fun `should return project when update project at MongoDB`() = runTest {
        val newProject = Project(
            id = ids[3],
            name = "Project 3",
        )
        val projectDTO = ProjectDTO(
            id = ids[3].toHexString(), name = "Project 3"
        )

        val createProject = remoteProjectDataSource.updateProject(newProject)

        coVerify(exactly = 1) { mongoClientCollection.replaceOne(Filters.eq(ID, newProject.id.toHexString()), projectDTO, any()) }

        assertThat(createProject).isEqualTo(newProject)
    }

    @Test
    fun `should throw ProjectNotChangedException exception when update project fails in MongoDB`() = runTest {
        val newProject = Project(
            id = ids[3],
            name = "Project 3",
        )
        val projectDTO = ProjectDTO(
            id = ids[3].toHexString(), name = "Project 3"
        )

        coEvery {
            mongoClientCollection.replaceOne(
                Filters.eq(ID, newProject.id.toHexString()),
                projectDTO,
                any()
            )
        } throws ProjectNotChangedException()

        assertThrows<ProjectNotChangedException> { remoteProjectDataSource.updateProject(newProject) }
    }

    @Test
    fun `should return project when get project by Id project at MongoDB`() = runTest {
        remoteProjectDataSource.getProjectById(ids[1])

        coVerify(exactly = 1) { mongoClientCollection.find(filter = any()) }
    }

    @Test
    fun `should throw GetProjectByIdFailedException exception when get project by by ID fails in MongoDB`() = runTest {
        coEvery { mongoClientCollection.find(filter = any()) } throws ProjectNotFoundException()

        assertThrows<ProjectNotFoundException> { remoteProjectDataSource.getProjectById(ids[1]) }
    }

    @Test
    fun `should delete project when delete project by Id at MongoDB`() = runTest {
        remoteProjectDataSource.deleteProject(ids[1])

        coVerify(exactly = 1) { mongoClientCollection.deleteOne(filter = any(), options = any()) }
    }

    @Test
    fun `should throw DeleteProjectFailedException exception when deleting project by ID fails in MongoDB`() = runTest {
        coEvery {
            mongoClientCollection.deleteOne(
                filter = any(),
                options = any()
            )
        } throws ProjectDeletionFailedException()

        assertThrows<ProjectDeletionFailedException> { remoteProjectDataSource.deleteProject(ids[1]) }
    }
}
