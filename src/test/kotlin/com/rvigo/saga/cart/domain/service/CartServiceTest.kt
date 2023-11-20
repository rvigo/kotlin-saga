package com.rvigo.saga.cart.domain.service

import com.rvigo.saga.cart.builder.CartBuilder
import com.rvigo.saga.cart.infra.repository.CartRepository
import com.rvigo.saga.lib.domain.message.event.Event
import com.rvigo.saga.lib.domain.message.event.EventDispatcher
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.util.Optional
import java.util.UUID

class CartServiceTest {

    private val cartRepository: CartRepository = mockk {
        every { save(any()) } answers {
            CartBuilder.build {
                this.id = firstArg()
            }
        }
        every { findById(any()) } answers {
            Optional.of(CartBuilder.build {
                this.id = firstArg()
            })
        }
    }

    private val eventDispatcher: EventDispatcher = mockk {
        justRun { emit(any<Event>()) }
    }

    private val service = CartService(cartRepository, eventDispatcher)

    @Test
    fun create() {

        service.create(UUID.randomUUID())

        verify {
            cartRepository.save(any())
            eventDispatcher.emit(any<Event>())
        }
    }

    @Test
    fun addItem() {

        service.addItem(UUID.randomUUID(), 1, 1)

        verify {
            cartRepository.findById(any())
            eventDispatcher.emit(any<Event>())
        }
    }

    @Test
    fun removeItem() {

        service.removeItem(UUID.randomUUID(), 1, 1)

        verify {
            cartRepository.findById(any())
            eventDispatcher.emit(any<Event>())
        }
    }

    @Test
    fun emptyCart() {

        service.emptyCart(UUID.randomUUID())

        verify {
            cartRepository.findById(any())
            eventDispatcher.emit(any<Event>())
        }
    }
}


