package com.biere.catalog.containers.api.dtos

interface ApiGeneralRegistrationMetadataDTOFactory {
    companion object {
        fun create(links: ApiGeneralRegistrationOperationsDTO): ApiGeneralRegistrationMetadataDTO {
            return ApiGeneralRegistrationMetadataDTO(links)
        }

        fun createOperations (
            self: ApiGeneralRegistrationActionDTO?,
            update: ApiGeneralRegistrationActionDTO?,
            delete: ApiGeneralRegistrationActionDTO?): ApiGeneralRegistrationOperationsDTO {
            return ApiGeneralRegistrationOperationsDTO(self, update, delete)
        }

        fun createAction(href: String, method: String, type: String = "application/json"): ApiGeneralRegistrationActionDTO {
            return ApiGeneralRegistrationActionDTO(href = href, method = method, type = type)
        }
    }
}