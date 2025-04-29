package org.example.logic.utils

class BlankInputException(message: String) : Exception(message)
class UserNotFoundException(message: String) : Exception(message)
class NoLoggedInUserException(message: String) : Exception(message)
class UserAlreadyExistsException(message: String) : Exception(message)
class ProjectNotChangedException(message: String) : Exception(message)
class ProjectNotFoundException(message: String) : Exception(message)
class TaskNotChangedException(message: String) : Exception(message)
class TaskNotFoundException(message: String) : Exception(message)
class StateNotFoundException(message: String) : Exception(message)
class InvalidInputException(message: String) : Exception(message)
class InsufficientPermissionsException(message: String) : Exception(message)
