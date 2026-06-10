<template>
  <nav class="navbar">
    <div class="container d-flex justify-content-between align-items-center">
      <RouterLink
        to="/"
        class="logo text-dark text-decoration-none fs-4 fw-bold"
        @click.prevent="irParaInicio"
      >
        vesteBem.
      </RouterLink>

      <!-- Barra de Menu em Cápsula (Centro) -->
      <div class="nav-menu-capsule d-none d-lg-flex">
        <RouterLink 
          to="/" 
          class="nav-menu-item" 
          :class="{ active: isItemActive('inicio') }"
          @click.prevent="irParaInicio"
        >
          Início
        </RouterLink>
        
        <div class="nav-menu-dropdown-wrapper">
          <a 
            href="#categorias-secao" 
            class="nav-menu-item" 
            :class="{ active: isItemActive('categorias') }"
            @click.prevent="irParaCategorias"
          >
            Categorias <i class="ph ph-caret-down ms-1"></i>
          </a>
          <div class="nav-dropdown-content">
            <div class="nav-dropdown-scroll">
              <RouterLink 
                v-for="cat in categorias" 
                :key="cat.id" 
                :to="{ path: '/produtos', query: { categoria: cat.id } }"
                class="nav-dropdown-link"
              >
                {{ cat.nome }}
              </RouterLink>
            </div>
          </div>
        </div>

        <RouterLink 
          to="/produtos" 
          class="nav-menu-item" 
          :class="{ active: isItemActive('produtos') }"
        >
          Produtos
        </RouterLink>
        
        <a 
          href="#promocoes" 
          class="nav-menu-item" 
          :class="{ active: isItemActive('promocoes') }"
          @click.prevent="irParaPromocoes"
        >
          Promoções
        </a>
        <a 
          href="#contato" 
          class="nav-menu-item" 
          :class="{ active: isItemActive('contato') }"
          @click.prevent="irParaContato"
        >
          Contato
        </a>
      </div>

      <div class="nav-icons d-flex gap-3 align-items-center">
        <span
          v-if="isAdmin"
          class="badge bg-primary rounded-pill px-3 py-2 fw-normal"
        >
          <i class="ph ph-shield-check me-1"></i>
          Admin
        </span>

        <template v-if="estaLogado">
          <div class="d-flex align-items-center gap-3">
            <RouterLink
              to="/perfil"
              title="Meu Perfil"
              class="nav-icon-link text-dark text-decoration-none d-flex align-items-center gap-2"
            >
              <i class="ph ph-user-circle fs-4"></i>
              <span class="user-name">Olá, {{ nomeUsuario }}</span>
            </RouterLink>

            <a
              @click.prevent="$emit('logout')"
              title="Sair"
              class="nav-icon-link text-dark fs-4"
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
          class="nav-icon-link text-dark fs-4"
        >
          <i class="ph ph-user"></i>
        </RouterLink>

        <RouterLink
          to="/carrinho"
          title="Carrinho"
          class="nav-icon-link text-dark fs-4 position-relative"
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
import { ref, onMounted, onUnmounted, watch } from "vue";
import { useRouter, useRoute } from "vue-router";
import api from "../../services/api.js";

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

const router = useRouter();
const route = useRoute();
const categorias = ref([]);
const activeSection = ref("inicio");

const carregarCategorias = async () => {
  try {
    const resposta = await api.get("/categorias");
    categorias.value = resposta.data;
  } catch (error) {
    console.error("Erro ao carregar categorias na navbar:", error);
  }
};

const handleScroll = () => {
  if (route.path !== "/") return;

  // Se estiver no final da página, ativa contato
  if (window.innerHeight + window.scrollY >= document.documentElement.scrollHeight - 80) {
    activeSection.value = "contato";
    return;
  }

  const scrollPosition = window.scrollY + 120; // offset do topo

  const categoriasEl = document.getElementById("categorias-secao");
  const produtosEl = document.querySelector(".produtos");
  const promocoesEl = document.getElementById("newsletter-secao");
  const contatoEl = document.getElementById("contato-secao");

  if (contatoEl && scrollPosition >= contatoEl.offsetTop) {
    activeSection.value = "contato";
  } else if (promocoesEl && scrollPosition >= promocoesEl.offsetTop) {
    activeSection.value = "promocoes";
  } else if (produtosEl && scrollPosition >= produtosEl.offsetTop) {
    activeSection.value = "produtos";
  } else if (categoriasEl && scrollPosition >= categoriasEl.offsetTop) {
    activeSection.value = "categorias";
  } else {
    activeSection.value = "inicio";
  }
};

const isItemActive = (item) => {
  if (route.path === "/") {
    return activeSection.value === item;
  }
  
  if (item === "produtos") {
    return route.path === "/produtos" && !route.query.categoria;
  }
  if (item === "categorias") {
    return route.path === "/produtos" && route.query.categoria;
  }
  return false;
};

onMounted(() => {
  carregarCategorias();
  window.addEventListener("scroll", handleScroll);
  setTimeout(handleScroll, 200); // Executa um delay para dar tempo do DOM montar
});

onUnmounted(() => {
  window.removeEventListener("scroll", handleScroll);
});

watch(() => route.path, (newPath) => {
  if (newPath !== "/") {
    activeSection.value = "";
  } else {
    setTimeout(handleScroll, 100);
  }
});

const irParaInicio = () => {
  if (route.path === "/") {
    window.scrollTo({ top: 0, behavior: "smooth" });
  } else {
    router.push("/");
  }
};

const irParaCategorias = () => {
  const el = document.getElementById("categorias-secao");
  if (el) {
    el.scrollIntoView({ behavior: "smooth" });
  } else {
    router.push("/#categorias-secao");
  }
};

const irParaPromocoes = () => {
  const el = document.getElementById("footer");
  if (el) {
    el.scrollIntoView({ behavior: "smooth" });
    const targetEl = document.getElementById("newsletter-secao");
    if (targetEl) {
      targetEl.classList.add("highlight-pulse");
      setTimeout(() => targetEl.classList.remove("highlight-pulse"), 2000);
    }
  } else {
    router.push("/#promocoes");
  }
};

const irParaContato = () => {
  const el = document.getElementById("footer");
  if (el) {
    el.scrollIntoView({ behavior: "smooth" });
    const targetEl = document.getElementById("contato-secao");
    if (targetEl) {
      targetEl.classList.add("highlight-pulse");
      setTimeout(() => targetEl.classList.remove("highlight-pulse"), 2000);
    }
  } else {
    router.push("/#contato");
  }
};
</script>

<style scoped>
.navbar {
  padding: 1rem 0;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.04);
  position: sticky;
  top: 0;
  z-index: 10100;
  transition: all 0.3s ease;
}

.logo {
  letter-spacing: -0.02em;
  transition: all 0.3s ease;
}

.logo:hover {
  opacity: 0.8;
}

/* Menu Central em Cápsula */
.nav-menu-capsule {
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.75);
  border: 1px solid rgba(0, 0, 0, 0.06);
  padding: 4px;
  border-radius: 100px;
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.02);
}

.nav-menu-item {
  color: var(--text-secondary, #86868b);
  font-size: 0.9rem;
  font-weight: 500;
  padding: 6px 16px;
  border-radius: 100px;
  text-decoration: none !important;
  transition: all 0.3s cubic-bezier(0.25, 1, 0.5, 1);
  display: inline-flex;
  align-items: center;
  cursor: pointer;
  border: none;
  background: transparent;
}

.nav-menu-item:hover {
  color: var(--text-primary, #1d1d1f);
  background: rgba(0, 0, 0, 0.04);
}

.nav-menu-item.active {
  background: var(--primary-color, #1a1a1a);
  color: #ffffff !important;
}

/* Dropdown de Categorias */
.nav-menu-dropdown-wrapper {
  position: relative;
}

.nav-dropdown-content {
  position: absolute;
  top: calc(100% + 8px);
  left: 50%;
  transform: translateX(-50%) translateY(10px) scale(0.95);
  opacity: 0;
  visibility: hidden;
  background: rgba(255, 255, 255, 0.95);
  border: 1px solid rgba(0, 0, 0, 0.08);
  border-radius: 16px;
  padding: 8px;
  min-width: 180px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  z-index: 10000;
  transition: all 0.25s cubic-bezier(0.25, 1, 0.5, 1);
}

.nav-menu-dropdown-wrapper:hover .nav-dropdown-content {
  opacity: 1;
  visibility: visible;
  transform: translateX(-50%) translateY(0) scale(1);
}

.nav-dropdown-scroll {
  max-height: 240px;
  overflow-y: auto;
  padding-right: 4px;
}

/* Scrollbar customizada premium */
.nav-dropdown-scroll::-webkit-scrollbar {
  width: 4px;
}

.nav-dropdown-scroll::-webkit-scrollbar-track {
  background: transparent;
}

.nav-dropdown-scroll::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.1);
  border-radius: 10px;
}

.nav-dropdown-scroll::-webkit-scrollbar-thumb:hover {
  background: rgba(0, 0, 0, 0.2);
}

.nav-dropdown-link {
  display: block;
  padding: 8px 16px;
  color: var(--text-primary, #1d1d1f);
  text-decoration: none !important;
  font-size: 0.85rem;
  font-weight: 500;
  border-radius: 10px;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.nav-dropdown-link:hover {
  background: rgba(0, 0, 0, 0.04);
  color: var(--primary-color, #1a1a1a);
}

/* Ícones de Ação */
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

.nav-icon-link {
  position: relative;
  text-decoration: none;
}

.nav-icon-link::after {
  content: "";
  position: absolute;
  width: 100%;
  height: 2px;
  bottom: -4px;
  left: 0;
  background-color: var(--primary-color, #1a1a1a);
  transform: scaleX(0);
  transform-origin: center;
  transition: transform 0.3s ease;
}

.nav-icon-link:hover::after {
  transform: scaleX(1);
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

@media (max-width: 992px) {
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
