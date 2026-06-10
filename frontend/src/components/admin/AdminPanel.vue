<template>
  <section class="admin-panel py-5 fade-in-up">
    <div class="container">
      <div class="admin-card p-4 rounded-4 shadow-sm bg-white">
        <div class="d-flex align-items-center mb-3">
          <i class="ph ph-wrench fs-2 me-3 admin-icon"></i>

          <h2 class="mb-0 fs-4 fw-bold">Gestão do Catálogo</h2>
        </div>

        <p class="text-muted mb-4">
          Adicione novas coleções e gerencie o estoque da loja.
        </p>

        <div class="d-flex gap-3 flex-wrap">
          <button
            @click="$emit('nova-categoria')"
            class="btn-premium d-flex align-items-center gap-2"
          >
            <i class="ph ph-tag fs-5"></i>
            Nova Categoria
          </button>

          <button
            @click="$emit('novo-produto')"
            class="btn-produto d-flex align-items-center gap-2"
          >
            <i class="ph ph-t-shirt fs-5"></i>
            Novo Produto
          </button>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
defineEmits(["nova-categoria", "novo-produto"]);
</script>

<style scoped>
.admin-panel {
  background-color: var(--surface-color);
}

.admin-card {
  border-left: 5px solid var(--primary-color);
  transition: all 0.3s ease;
}

.admin-card:hover {
  transform: translateY(-2px);

  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.06) !important;
}

.admin-icon {
  color: var(--primary-color);
}

.btn-produto {
  border: none;

  padding: 0.85rem 1.5rem;

  border-radius: var(--radius-md);

  background-color: var(--text-secondary);

  color: white;

  font-weight: 500;

  transition: all 0.3s ease;
}

.btn-produto:hover {
  transform: translateY(-2px);

  opacity: 0.95;
}

.fade-in-up {
  animation: fadeInUp 0.8s cubic-bezier(0.25, 1, 0.5, 1) forwards;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(25px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 768px) {
  .admin-card {
    text-align: center;
  }

  .admin-card .d-flex:first-child {
    justify-content: center;
  }

  .admin-card .d-flex:last-child {
    justify-content: center;
  }
}
</style>
