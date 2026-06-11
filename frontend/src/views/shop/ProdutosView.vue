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

    <CarrinhoFlutuante
      :quantidade="quantidadeCarrinho"
      @abrir="sidebarAberta = true"
    />

    <CarrinhoSidebar
      :is-open="sidebarAberta"
      :itens="itensCarrinho"
      :subtotal="subtotalCarrinho"
      :usuario-id="usuarioId"
      @close="sidebarAberta = false"
      @adicionar-item="adicionarItemSidebar"
      @remover-item="removerItemSidebar"
      @carrinho-atualizado="carregarCarrinho"
    />

    <LoginRequeridoModal
      :aberto="modalLoginAberto"
      @fechar="modalLoginAberto = false"
    />

    <Footer />
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from "vue";
import { useRouter, useRoute, onBeforeRouteLeave } from "vue-router";

import Navbar from "../../components/layout/Navbar.vue";
import ProdutoList from "../../components/produto/ProdutoList.vue";
import CarrinhoSidebar from "../../components/layout/CarrinhoSidebar.vue";
import CarrinhoFlutuante from "../../components/layout/CarrinhoFlutuante.vue";
import Footer from "../../components/layout/Footer.vue";
import LoginRequeridoModal from "../../components/modal/LoginRequeridoModal.vue";

import { useAuth } from "../../composables/useAuth.js";
import { useCarrinho } from "../../composables/useCarrinho.js";
import api from "../../services/api.js";

const router = useRouter();
const route = useRoute();

const {
  estaLogado,
  isAdmin,
  nomeUsuario,
  usuarioId,
  carregarUsuario,
  fazerLogout,
} = useAuth();
const {
  itensCarrinho,
  subtotalCarrinho,
  sidebarAberta,
  modalLoginAberto,
  quantidadeCarrinho,
  carregarCarrinho,
  adicionarAoCarrinho,
  adicionarItemSidebar,
  removerItemSidebar,
} = useCarrinho(usuarioId, estaLogado);

const produtos = ref([]);
const categorias = ref([]);
const filtroNomeAtual = ref("");
const filtroCategoriasAtual = ref([]);

const carregarCategorias = async () => {
  try {
    const resposta = await api.get("/categorias");
    categorias.value = resposta.data;
  } catch (error) {
    console.error("Erro ao carregar categorias:", error);
  }
};

const carregarProdutos = async () => {
  try {
    const params = new URLSearchParams();
    if (filtroNomeAtual.value) params.append("nome", filtroNomeAtual.value);
    filtroCategoriasAtual.value.forEach((id) =>
      params.append("categoriasIds", id),
    );

    const queryStr = params.toString();
    const url = queryStr ? `/produtos?${queryStr}` : "/produtos";
    const response = await api.get(url);
    produtos.value = response.data;

    setTimeout(() => {
      const isReturning = sessionStorage.getItem("is_returning");
      if (isReturning === "true") {
        const scrollPos = sessionStorage.getItem(
          "scroll_position_" + route.path,
        );
        if (scrollPos) window.scrollTo(0, parseInt(scrollPos));
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
  if (categoriasIds?.length > 0) query.categoria = categoriasIds[0];

  router.push({ path: route.path, query });
  carregarProdutos();
};

watch(
  () => [route.query.categoria, route.query.nome],
  ([newCat, newNome]) => {
    filtroCategoriasAtual.value = newCat
      ? [Number(newCat)].filter((n) => !isNaN(n))
      : [];
    filtroNomeAtual.value = newNome || "";
    carregarProdutos();
  },
);

onBeforeRouteLeave((to, from) => {
  sessionStorage.setItem("scroll_position_" + from.path, window.scrollY);
});

onMounted(async () => {
  await carregarUsuario();
  if (usuarioId.value) await carregarCarrinho();

  if (route.query.categoria) {
    const catId = Number(route.query.categoria);
    if (!isNaN(catId)) filtroCategoriasAtual.value = [catId];
  }
  if (route.query.nome) filtroNomeAtual.value = route.query.nome;

  await Promise.all([carregarCategorias(), carregarProdutos()]);
});
</script>

<style scoped>
.produtos-view-wrapper {
  min-height: 100vh;
  background-color: var(--bg-color, #ffffff);
}
</style>
