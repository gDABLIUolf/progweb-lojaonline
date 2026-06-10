<template>
  <div class="produtos-view-wrapper">
    <Navbar
      :esta-logado="estaLogado"
      :is-admin="isAdmin"
      :nome-usuario="nomeUsuario"
      :quantidade-carrinho="quantidadeCarrinho"
      @logout="fazerLogout"
    />

    <ProdutoList
      :produtos="produtos"
      :categorias="categorias"
      :is-catalogo-page="true"
      :filtro-categorias-inicial="filtroCategoriasAtual"
      :filtro-nome-inicial="filtroNomeAtual"
      @adicionar-carrinho="adicionarAoCarrinho"
      @filtrar="aplicarFiltros"
    />

    <!-- Botão flutuante para abrir o carrinho -->
    <button
      class="btn-carrinho-flutuante"
      @click="sidebarAberta = true"
      title="Ver carrinho"
    >
      <i class="ph ph-handbag"></i>
      <span v-if="quantidadeCarrinho > 0" class="flutuante-count">
        {{ quantidadeCarrinho }}
      </span>
    </button>

    <!-- Sidebar do Carrinho -->
    <CarrinhoSidebar
      :is-open="sidebarAberta"
      :itens="itensCarrinho"
      :subtotal="subtotalCarrinho"
      @close="sidebarAberta = false"
      @adicionar-item="adicionarItemSidebar"
      @remover-item="removerItemSidebar"
    />

    <!-- Modal de login necessário -->
    <div
      v-if="modalLoginAberto"
      class="modal-overlay"
      @click.self="modalLoginAberto = false"
    >
      <div class="modal-content fade-in-up text-center">
        <i
          class="ph ph-lock"
          style="font-size: 3rem; color: var(--text-secondary)"
        ></i>
        <h4 class="fw-bold mt-3">Faça login para comprar</h4>
        <p class="text-muted mb-4">
          Você precisa estar logado para adicionar produtos ao carrinho.
        </p>
        <RouterLink
          to="/login"
          class="btn-premium w-100 text-decoration-none d-block mb-3"
        >
          Fazer Login
        </RouterLink>
        <button
          class="btn btn-link text-muted text-decoration-none"
          @click="modalLoginAberto = false"
        >
          Continuar navegando
        </button>
      </div>
    </div>

    <!-- Rodapé Premium -->
    <Footer />
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from "vue";
import { useRouter, useRoute, onBeforeRouteLeave } from "vue-router";

import Navbar from "../../components/layout/Navbar.vue";
import ProdutoList from "../../components/produto/ProdutoList.vue";
import CarrinhoSidebar from "../../components/layout/CarrinhoSidebar.vue";
import Footer from "../../components/layout/Footer.vue";
import api from "../../services/api.js";

const router = useRouter();
const route = useRoute();
const estaLogado = ref(false);
const isAdmin = ref(false);
const nomeUsuario = ref("");

const usuarioId = ref(null);
const itensCarrinho = ref([]);
const subtotalCarrinho = ref(0);
const sidebarAberta = ref(false);
const modalLoginAberto = ref(false);

const quantidadeCarrinho = computed(() => {
  return itensCarrinho.value.length;
});

const decodificarJWT = (token) => {
  try {
    const payload = token.split(".")[1];
    const base64 = payload.replace(/-/g, "+").replace(/_/g, "/");
    const jsonPayload = decodeURIComponent(
      window
        .atob(base64)
        .split("")
        .map((c) => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2))
        .join(""),
    );
    return JSON.parse(jsonPayload);
  } catch {
    return null;
  }
};

const carregarUsuario = () => {
  const token = localStorage.getItem("token_vestebem");
  if (!token) return;

  estaLogado.value = true;
  const dadosToken = decodificarJWT(token);

  if (!dadosToken) return;

  usuarioId.value = dadosToken.usuarioId || dadosToken.id || null;

  if (dadosToken.sub) {
    nomeUsuario.value = dadosToken.sub.split("@")[0];
  }

  const permissao = dadosToken.role || "";
  isAdmin.value = permissao.toUpperCase() === "ADMIN";
};

const carregarCarrinho = async () => {
  if (!usuarioId.value) return;
  try {
    const resposta = await api.get(`/carrinhos/${usuarioId.value}`);
    itensCarrinho.value = resposta.data.itens || [];
    subtotalCarrinho.value = resposta.data.subtotal || 0;
  } catch {
    itensCarrinho.value = [];
    subtotalCarrinho.value = 0;
  }
};

const fazerLogout = () => {
  localStorage.removeItem("token_vestebem");
  estaLogado.value = false;
  isAdmin.value = false;
  nomeUsuario.value = "";
  usuarioId.value = null;
  itensCarrinho.value = [];
  subtotalCarrinho.value = 0;
  router.push("/");
};

const adicionarAoCarrinho = async (produtoId) => {
  if (!estaLogado.value) {
    modalLoginAberto.value = true;
    return;
  }

  try {
    const resposta = await api.post(`/carrinhos/${usuarioId.value}/adicionar`, {
      produtoId,
      quantidade: 1,
    });
    itensCarrinho.value = resposta.data.itens || [];
    subtotalCarrinho.value = resposta.data.subtotal || 0;
    sidebarAberta.value = true;
  } catch (error) {
    alert(error.response?.data || "Erro ao adicionar ao carrinho.");
  }
};

const removerItemSidebar = async (produtoId) => {
  try {
    const resposta = await api.delete(
      `/carrinhos/${usuarioId.value}/remover/${produtoId}`
    );
    itensCarrinho.value = resposta.data.itens || [];
    subtotalCarrinho.value = resposta.data.subtotal || 0;
  } catch (error) {
    alert(error.response?.data || "Erro ao remover item.");
  }
};

const adicionarItemSidebar = async (produtoId) => {
  try {
    const resposta = await api.post(`/carrinhos/${usuarioId.value}/adicionar`, {
      produtoId,
      quantidade: 1,
    });
    itensCarrinho.value = resposta.data.itens || [];
    subtotalCarrinho.value = resposta.data.subtotal || 0;
  } catch (error) {
    alert(error.response?.data || "Erro ao adicionar item.");
  }
};

const categorias = ref([]);
const carregarCategorias = async () => {
  try {
    const resposta = await api.get("/categorias");
    categorias.value = resposta.data;
  } catch (error) {
    console.error("Erro:", error);
  }
};

const produtos = ref([]);
const filtroNomeAtual = ref("");
const filtroCategoriasAtual = ref([]);

const carregarProdutos = async () => {
  try {
    const params = new URLSearchParams();
    if (filtroNomeAtual.value) {
      params.append("nome", filtroNomeAtual.value);
    }
    if (filtroCategoriasAtual.value && filtroCategoriasAtual.value.length > 0) {
      filtroCategoriasAtual.value.forEach(id => {
        params.append("categoriasIds", id);
      });
    }
    const queryStr = params.toString();
    const url = queryStr ? `/produtos?${queryStr}` : "/produtos";
    const response = await api.get(url);
    produtos.value = response.data;

    // Restaura a posição de scroll salva se estiver retornando de um produto
    setTimeout(() => {
      const isReturning = sessionStorage.getItem("is_returning");
      if (isReturning === "true") {
        const scrollPos = sessionStorage.getItem("scroll_position_" + route.path);
        if (scrollPos) {
          window.scrollTo(0, parseInt(scrollPos));
        }
        sessionStorage.removeItem("is_returning");
      }
    }, 50);
  } catch (error) {
    console.error("Erro ao carregar produtos:", error);
  }
};

const aplicarFiltros = ({ nome, categoriasIds }) => {
  filtroNomeAtual.value = nome;
  filtroCategoriasAtual.value = categoriasIds;

  const query = {};
  if (nome) query.nome = nome;
  if (categoriasIds && categoriasIds.length > 0) {
    query.categoria = categoriasIds[0];
  }
  
  router.push({ path: route.path, query });
  carregarProdutos();
};

const verificarHashEScroll = () => {
  const hash = window.location.hash;
  if (hash === "#promocoes" || hash === "#contato" || hash === "#categorias-secao") {
    const el = document.getElementById("footer");
    if (el) {
      el.scrollIntoView({ behavior: "smooth" });
      const targetId = hash === "#promocoes" ? "newsletter-secao" : "contato-secao";
      const targetEl = document.getElementById(targetId);
      if (targetEl) {
        targetEl.classList.add("highlight-pulse");
        setTimeout(() => targetEl.classList.remove("highlight-pulse"), 2000);
      }
    }
  }
};

onBeforeRouteLeave((to, from) => {
  sessionStorage.setItem("scroll_position_" + from.path, window.scrollY);
});

watch(
  () => [route.query.categoria, route.query.nome],
  ([newCat, newNome]) => {
    if (newCat) {
      const catId = Number(newCat);
      filtroCategoriasAtual.value = !isNaN(catId) ? [catId] : [];
    } else {
      filtroCategoriasAtual.value = [];
    }
    filtroNomeAtual.value = newNome || "";
    carregarProdutos();
  }
);

onMounted(async () => {
  carregarUsuario();
  if (usuarioId.value) {
    await carregarCarrinho();
  }

  if (route.query.categoria) {
    const catId = Number(route.query.categoria);
    if (!isNaN(catId)) {
      filtroCategoriasAtual.value = [catId];
    }
  }
  if (route.query.nome) {
    filtroNomeAtual.value = route.query.nome;
  }

  await Promise.all([carregarCategorias(), carregarProdutos()]);

  setTimeout(verificarHashEScroll, 100);
});
</script>

<style scoped>
.produtos-view-wrapper {
  min-height: 100vh;
  background-color: var(--bg-color, #ffffff);
}

.btn-carrinho-flutuante {
  position: fixed;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
  background: var(--primary-color, #111);
  color: white;
  border: none;
  border-radius: 12px 0 0 12px;
  width: 48px;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.4rem;
  cursor: pointer;
  z-index: 9997;
  box-shadow: -4px 0 20px rgba(0, 0, 0, 0.15);
  transition: width 0.2s ease;
}

.btn-carrinho-flutuante:hover {
  width: 56px;
}

.flutuante-count {
  position: absolute;
  top: 6px;
  right: 6px;
  background: #e53e3e;
  color: white;
  font-size: 0.65rem;
  font-weight: 700;
  width: 16px;
  height: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10000;
}

.modal-content {
  background: var(--bg-color, #ffffff);
  width: 100%;
  max-width: 450px;
  border-radius: var(--radius-lg, 16px);
  padding: 2.5rem;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.fade-in-up {
  animation: fadeInUp 0.8s cubic-bezier(0.25, 1, 0.5, 1) forwards;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(24px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
