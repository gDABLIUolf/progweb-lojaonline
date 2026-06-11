<template>
  <div class="carrinho-card p-4 rounded-4 shadow-sm bg-white sticky-summary">
    <h4 class="fw-bold mb-4">Resumo do Pedido</h4>

    <!-- Nenhum item selecionado -->
    <div v-if="itensSelecionados.length === 0" class="aviso-selecao mb-4">
      <i class="ph ph-info me-2"></i>
      Selecione ao menos um item para finalizar o pedido.
    </div>

    <div v-else>
      <!-- Lista resumida dos itens selecionados -->
      <div class="itens-resumo mb-3">
        <div
          v-for="item in itensParaCheckout"
          :key="item.itemId"
          class="d-flex justify-content-between align-items-center mb-2 pequeno"
        >
          <span class="text-muted text-truncate" style="max-width: 160px">
            {{ item.produtoNome }} <span class="badge-qty">×{{ item.quantidade }}</span>
          </span>
          <span class="fw-semibold">R$ {{ item.precoTotal.toFixed(2) }}</span>
        </div>
      </div>

      <hr />

      <div class="d-flex justify-content-between mb-2">
        <span class="text-muted">Subtotal</span>
        <span class="fw-bold">R$ {{ subtotal.toFixed(2) }}</span>
      </div>
      <div class="d-flex justify-content-between mb-4">
        <span class="text-muted">Frete</span>
        <span class="text-success fw-bold">Grátis</span>
      </div>

      <hr />

      <div class="d-flex justify-content-between mb-4">
        <span class="fw-bold fs-5">Total</span>
        <span class="fw-bold fs-5" style="color: var(--primary-color)">
          R$ {{ subtotal.toFixed(2) }}
        </span>
      </div>
    </div>

    <button
      class="btn-premium w-100"
      :disabled="finalizando || itensSelecionados.length === 0"
      @click="$emit('finalizar')"
    >
      <span v-if="finalizando">
        <span class="spinner-border spinner-border-sm me-2"></span>A processar...
      </span>
      <span v-else>
        Finalizar
        {{ itensSelecionados.length > 0
          ? `(${itensSelecionados.length} ${itensSelecionados.length === 1 ? 'item' : 'itens'})`
          : '' }}
      </span>
    </button>

    <RouterLink
      to="/"
      class="d-block text-center mt-3 text-muted text-decoration-none"
      style="font-size: 0.9rem"
    >
      <i class="ph ph-arrow-left me-1"></i> Continuar a Comprar
    </RouterLink>
  </div>
</template>

<script setup>
defineProps({
  itensSelecionados: {
    type: Array,
    default: () => [],
  },
  itensParaCheckout: {
    type: Array,
    default: () => [],
  },
  subtotal: {
    type: Number,
    default: 0,
  },
  finalizando: {
    type: Boolean,
    default: false,
  },
});

defineEmits(["finalizar"]);
</script>

<style scoped>
.carrinho-card {
  border: 1px solid #f1f5f9;
}

.sticky-summary {
  position: sticky;
  top: 90px;
}

.aviso-selecao {
  background: #fef9ec;
  border: 1px solid #f5c75e;
  border-radius: 10px;
  padding: 0.75rem 1rem;
  font-size: 0.85rem;
  color: #7a5c00;
}

.itens-resumo {
  max-height: 180px;
  overflow-y: auto;
  scrollbar-width: thin;
}

.pequeno {
  font-size: 0.85rem;
}

.badge-qty {
  display: inline-block;
  background: #f1f5f9;
  color: #64748b;
  border-radius: 6px;
  padding: 0 5px;
  font-size: 0.75rem;
  font-weight: 700;
}
</style>
