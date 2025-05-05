package org.example.data.source.remote.mongo

import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.example.data.mapper.toProject
import org.example.data.mapper.toProjectDTO
import org.example.data.models.ProjectDTO
import org.example.data.source.remote.contract.RemoteProjectDataSource
import org.example.logic.models.Project

class MongoProjectDataSource(
    private val mongoClient: MongoCollection<ProjectDTO>
) : RemoteProjectDataSource {
    override suspend fun createProject(project: Project): Project {
        mongoClient.insertOne(project.toProjectDTO())
        return project
    }

    override suspend fun updateProject(updatedProject: Project): Project {
        mongoClient.replaceOne(Filters.eq("id", updatedProject.id), updatedProject.toProjectDTO())
        return updatedProject
    }

    override suspend fun deleteProject(projectId: String) {
        mongoClient.deleteOne(Filters.eq("id", projectId))
    }

    override suspend fun getAllProjects(): List<Project> {
        return mongoClient.find().toList().map { it.toProject()}

    }

    override suspend fun getProjectById(projectId: String): Project? {
        return mongoClient.find(Filters.eq("id", projectId)).firstOrNull()?.toProject()

    }
}