<template>
  <div class="produto-detalhes-wrapper">
    <Navbar
      :esta-logado="estaLogado"
      :is-admin="isAdmin"
      :nome-usuario="nomeUsuario"
      :quantidade-carrinho="quantidadeCarrinho"
      @logout="fazerLogout"
    />

    <div class="container py-5">
      <div v-if="carregando" class="text-center py-5">
        <div class="spinner-border text-dark" role="status"></div>
        <p class="mt-3 text-muted">A carregar detalhes do produto...</p>
      </div>

      <div v-else-if="!produto" class="text-center py-5">
        <i class="ph ph-warning-circle fs-1 text-danger mb-3"></i>
        <h3 class="fw-bold">Produto não encontrado</h3>
        <RouterLink to="/" class="btn btn-dark mt-3">Voltar ao Catálogo</RouterLink>
      </div>

      <div v-else class="row g-5">
        <div class="col-lg-6">
          <ProdutoGaleria :produto="produto" />
        </div>
        <div class="col-lg-6">
          <ProdutoDetalhes
            :produto="produto"
            @adicionar-carrinho="adicionarAoCarrinho"
          />
        </div>
      </div>

      <ProdutoAvaliacoes
        v-if="produto"
        :avaliacoes="produto.avaliacoes"
      />
    </div>

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
import { ref, onMounted } from "vue";
import { useRoute, onBeforeRouteLeave } from "vue-router";

import Navbar from "../../components/layout/Navbar.vue";
import CarrinhoSidebar from "../../components/layout/CarrinhoSidebar.vue";
import CarrinhoFlutuante from "../../components/layout/CarrinhoFlutuante.vue";
import Footer from "../../components/layout/Footer.vue";
import LoginRequeridoModal from "../../components/modal/LoginRequeridoModal.vue";
import ProdutoGaleria from "../../components/produto/ProdutoGaleria.vue";
import ProdutoDetalhes from "../../components/produto/ProdutoDetalhes.vue";
import ProdutoAvaliacoes from "../../components/produto/ProdutoAvaliacoes.vue";

import { useAuth } from "../../composables/useAuth.js";
import { useCarrinho } from "../../composables/useCarrinho.js";
import api from "../../services/api";

const route = useRoute();

const { estaLogado, isAdmin, nomeUsuario, usuarioId, carregarUsuario, fazerLogout } = useAuth();
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


const carregando = ref(true);
const produto = ref(null);

const carregarProduto = async () => {
  carregando.value = true;
  try {
    const response = await api.get(`/produtos/${route.params.id}`);
    produto.value = response.data;
  } catch (error) {
    console.error("Erro ao carregar detalhes do produto:", error);
  } finally {
    carregando.value = false;
  }
};

onBeforeRouteLeave(() => {
  sessionStorage.setItem("is_returning", "true");
});

onMounted(async () => {
  await carregarUsuario();
  if (usuarioId.value) await carregarCarrinho();
  await carregarProduto();
});
</script>

<style scoped>
.produto-detalhes-wrapper {
  min-height: 100vh;
  background-color: var(--surface-color);
}
</style>
