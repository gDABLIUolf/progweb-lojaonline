<template>
  <div class="sidebar-container">
    <!-- Overlay escuro atrás do sidebar -->
    <div
      v-if="isOpen"
      class="sidebar-overlay"
      @click="$emit('close')"
    ></div>

    <!-- Sidebar do Carrinho -->
    <div
      class="carrinho-sidebar"
      :class="{ 'sidebar-open': isOpen }"
    >
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h5 class="fw-bold mb-0">
          <i class="ph ph-handbag me-2"></i>Meu Carrinho
        </h5>
        <button class="btn-close-modal" @click="$emit('close')">
          <i class="ph ph-x"></i>
        </button>
      </div>

      <!-- Carrinho vazio -->
      <div v-if="itens.length === 0" class="text-center py-5">
        <i
          class="ph ph-handbag"
          style="font-size: 3rem; color: var(--text-secondary)"
        ></i>
        <p class="mt-3 text-muted">O seu carrinho está vazio.</p>
      </div>

      <!-- Itens do carrinho -->
      <div v-else class="d-flex flex-column h-100">
        <div class="sidebar-itens flex-grow-1">
          <div
            v-for="item in itens"
            :key="item.itemId"
            class="sidebar-item d-flex align-items-center gap-3 py-3"
          >
            <img
              :src="`http://localhost:8080/api/produtos/${item.produtoId}/imagem`"
              :alt="item.produtoNome"
              class="sidebar-item-img rounded-2"
            />
            <div class="flex-grow-1">
              <p class="fw-bold mb-0 small">{{ item.produtoNome }}</p>
              <p class="text-muted mb-1" style="font-size: 0.8rem">
                R$ {{ item.precoUnitario.toFixed(2) }}
              </p>
              <div class="d-flex align-items-center gap-2">
                <button
                  class="btn-qty-sm"
                  @click="$emit('remover-item', item.produtoId)"
                >
                  <i class="ph ph-minus"></i>
                </button>
                <span class="fw-bold small">{{ item.quantidade }}</span>
                <button
                  class="btn-qty-sm"
                  @click="$emit('adicionar-item', item.produtoId)"
                >
                  <i class="ph ph-plus"></i>
                </button>
              </div>
            </div>
            <p class="fw-bold mb-0 small" style="color: var(--primary-color)">
              R$ {{ item.precoTotal.toFixed(2) }}
            </p>
          </div>
        </div>

        <div class="sidebar-footer mt-auto pt-3">
          <div class="d-flex justify-content-between mb-3">
            <span class="text-muted">Subtotal</span>
            <span class="fw-bold">R$ {{ subtotal.toFixed(2) }}</span>
          </div>
          <RouterLink
            to="/carrinho"
            class="btn-premium w-100 text-center text-decoration-none d-block"
            @click="$emit('close')"
          >
            Finalizar Pedido
          </RouterLink>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  isOpen: {
    type: Boolean,
    default: false,
  },
  itens: {
    type: Array,
    default: () => [],
  },
  subtotal: {
    type: Number,
    default: 0,
  },
});

defineEmits(["close", "adicionar-item", "remover-item"]);
</script>

<style scoped>
.carrinho-sidebar {
  position: fixed;
  top: 0;
  right: -420px;
  width: 400px;
  height: 100vh;
  background: white;
  box-shadow: -10px 0 40px rgba(0, 0, 0, 0.1);
  z-index: 9999;
  padding: 2rem;
  transition: right 0.3s cubic-bezier(0.25, 1, 0.5, 1);
  display: flex;
  flex-direction: column;
  text-align: left;
}

.sidebar-open {
  right: 0;
}

.sidebar-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(2px);
  z-index: 9998;
}

.sidebar-itens {
  overflow-y: auto;
  max-height: calc(100vh - 220px);
}

.sidebar-item {
  border-bottom: 1px solid #f1f5f9;
}

.sidebar-item:last-child {
  border-bottom: none;
}

.sidebar-item-img {
  width: 60px;
  height: 60px;
  object-fit: cover;
  flex-shrink: 0;
}

.sidebar-footer {
  border-top: 1px solid #f1f5f9;
}

.btn-close-modal {
  background: transparent;
  border: none;
  font-size: 1.5rem;
  color: var(--text-secondary);
  cursor: pointer;
  transition: color 0.2s;
}

.btn-close-modal:hover {
  color: var(--text-primary);
}

.btn-qty-sm {
  width: 24px;
  height: 24px;
  border: 1px solid #e2e8f0;
  border-radius: 50%;
  background: white;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 0.75rem;
  transition: all 0.2s;
}

.btn-qty-sm:hover {
  background: var(--primary-color);
  color: white;
  border-color: var(--primary-color);
}
</style>
