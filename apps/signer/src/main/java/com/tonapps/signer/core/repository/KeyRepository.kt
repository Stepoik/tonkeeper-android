package com.tonapps.signer.core.repository

import com.tonapps.signer.core.entities.KeyEntity
import com.tonapps.signer.core.Scope
import com.tonapps.signer.core.source.KeyDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.ton.api.pub.PublicKeyEd25519

class KeyRepository(
    private val dataSource: KeyDataSource,
) {

    private val _keysEntityFlow = MutableStateFlow<List<KeyEntity>?>(null)

    val stream = _keysEntityFlow.asStateFlow().filterNotNull()

    init {
        Scope.repositories.launch(Dispatchers.IO) {
            _keysEntityFlow.value = dataSource.getEntities()
        }
    }

    fun clear() {
        dataSource.clear()
        _keysEntityFlow.value = emptyList()
    }

    suspend fun deleteKey(id: Long): Int = withContext(Dispatchers.IO) {
        dataSource.delete(id)

        _keysEntityFlow.value = _keysEntityFlow.value?.filter { it.id != id }
        _keysEntityFlow.value?.size ?: 0
    }

    fun findIdByPublicKey(publicKey: PublicKeyEd25519): Flow<Long> = flow {
        val id = dataSource.findIdByPublicKey(publicKey)
        emit(id)
    }.filterNotNull()

    suspend fun setName(id: Long, name: String) = withContext(Dispatchers.IO) {
        dataSource.setName(id, name)

        _keysEntityFlow.value = _keysEntityFlow.value?.map { entity ->
            if (entity.id == id) {
                entity.copy(name = name)
            } else {
                entity
            }
        }
    }

    fun getKey(id: Long): Flow<KeyEntity?> = stream.map { entities ->
        entities.firstOrNull { it.id == id }
    }.distinctUntilChanged()

    suspend fun addKey(
        name: String,
        publicKey: PublicKeyEd25519
    ): KeyEntity = withContext(Dispatchers.IO) {
        val entity = KeyEntity(
            id = dataSource.add(name, publicKey),
            name = name,
            publicKey = publicKey,
        )

        val currentList = _keysEntityFlow.value ?: emptyList()
        _keysEntityFlow.value = currentList + entity

        entity
    }


}