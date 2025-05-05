package org.example.data.source.remote.mongo

import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.example.data.mapper.toProject
import org.example.data.mapper.toProjectDTO
import org.example.data.models.ProjectDTO
import org.example.data.source.remote.contract.RemoteProjectDataSource
import org.example.data.utils.Constants.ID
import org.example.logic.models.Project
import org.example.logic.utils.*

class MongoProjectDataSource(
    private val mongoClient: MongoCollection<ProjectDTO>,
) : RemoteProjectDataSource {
    override suspend fun createProject(project: Project): Project {
        try {
            mongoClient.insertOne(project.toProjectDTO())
            return project
        } catch (e: Exception) {
            throw CreationItemFailedException("project creation failed")
        }
    }

    override suspend fun updateProject(updatedProject: Project): Project {
        try {
            mongoClient.replaceOne(Filters.eq(ID, updatedProject.id), updatedProject.toProjectDTO())
            return updatedProject
        } catch (e: Exception) {
            throw UpdateItemFailedException("project update failed")

        }
    }

    override suspend fun deleteProject(projectId: String) {
        try {
            mongoClient.deleteOne(Filters.eq(ID, projectId))
        } catch (e: Exception) {
            throw DeleteItemFailedException("project delete failed")
        }
    }

    override suspend fun getAllProjects(): List<Project> {
        try {
            return mongoClient.find().toList().map { it.toProject() }
        } catch (e: Exception) {
            throw GetItemsFailedException("projects get failed")
        }
    }

    override suspend fun getProjectById(projectId: String): Project? {
        try {
            return mongoClient.find(Filters.eq(ID, projectId)).firstOrNull()?.toProject()
        } catch (e: Exception) {
            throw GetItemByIdFailedException("project get by id failed")
        }
    }
}