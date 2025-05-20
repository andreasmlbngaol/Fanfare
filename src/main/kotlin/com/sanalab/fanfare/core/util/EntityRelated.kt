package com.sanalab.fanfare.core.util

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext

abstract class EntityRelated {
    @PersistenceContext
    protected lateinit var entityManager: EntityManager

    inline fun <reified T : Any> EntityManager.getReference(id: Any): T {
        return this.getReference(T::class.java, id)
    }

    inline fun <reified T : Any> EntityManager.findReference(id: Any): T? {
        return this.find(T::class.java, id)
    }
}
