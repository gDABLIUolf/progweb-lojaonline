<template>
  <nav class="navbar">
    <div class="container d-flex justify-content-between align-items-center">
      <RouterLink
        to="/"
        class="logo text-dark text-decoration-none fs-4 fw-bold"
      >
        vesteBem.
      </RouterLink>

      <div class="nav-icons d-flex gap-3 align-items-center">
        <span
          v-if="isAdmin"
          class="badge bg-primary rounded-pill px-3 py-2 fw-normal"
        >
          <i class="ph ph-shield-check me-1"></i>
          Admin
        </span>

        <a href="#" title="Pesquisar" class="text-dark fs-4">
          <i class="ph ph-magnifying-glass"></i>
        </a>

        <template v-if="estaLogado">
          <div class="d-flex align-items-center gap-2">
            <span class="user-name"> Olá, {{ nomeUsuario }} </span>

            <a
              @click.prevent="$emit('logout')"
              title="Sair"
              class="text-dark fs-4 ms-2"
              style="cursor: pointer"
            >
              <i class="ph ph-sign-out"></i>
            </a>
          </div>
        </template>

        <RouterLink
          v-else
          to="/login"
          title="Minha Conta"
          class="text-dark fs-4"
        >
          <i class="ph ph-user"></i>
        </RouterLink>

        <RouterLink
          to="/carrinho"
          title="Carrinho"
          class="text-dark fs-4 position-relative"
        >
          <i class="ph ph-handbag"></i>

          <span v-if="quantidadeCarrinho > 0" class="cart-count">
            {{ quantidadeCarrinho }}
          </span>
        </RouterLink>
      </div>
    </div>
  </nav>
</template>

<script setup>
defineProps({
  estaLogado: {
    type: Boolean,
    default: false,
  },

  isAdmin: {
    type: Boolean,
    default: false,
  },

  nomeUsuario: {
    type: String,
    default: "",
  },

  quantidadeCarrinho: {
    type: Number,
    default: 0,
  },
});

defineEmits(["logout"]);
</script>

<style scoped>
.navbar {
  padding: 1.5rem 0;
  background: transparent;
}

.logo {
  letter-spacing: -0.02em;
  transition: all 0.3s ease;
}

.logo:hover {
  opacity: 0.8;
}

.nav-icons a,
.nav-icons .router-link-active,
.nav-icons .router-link-exact-active {
  cursor: pointer;
  display: inline-block;
  transition: all 0.3s cubic-bezier(0.25, 1, 0.5, 1);
}

.nav-icons a:hover,
.nav-icons .router-link-active:hover,
.nav-icons .router-link-exact-active:hover {
  color: var(--primary-color) !important;
  transform: translateY(-4px);
}

.user-name {
  font-size: 0.95rem;
  font-weight: 500;
  color: var(--text-primary);
}

.cart-count {
  position: absolute;
  top: -5px;
  right: -8px;

  background: var(--primary-color);
  color: white;

  width: 18px;
  height: 18px;

  border-radius: 50%;

  display: flex;
  align-items: center;
  justify-content: center;

  font-size: 0.7rem;
  font-weight: 600;
}

.badge {
  font-size: 0.85rem;
}

@media (max-width: 768px) {
  .navbar {
    padding: 1rem 0;
  }

  .user-name {
    display: none;
  }

  .badge {
    display: none;
  }
}
</style>
