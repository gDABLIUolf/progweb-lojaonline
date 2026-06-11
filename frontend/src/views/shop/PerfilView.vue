<template>
  <div class="perfil-wrapper">
    <Navbar
      :esta-logado="estaLogado"
      :is-admin="isAdmin"
      :nome-usuario="nomeUsuario"
      :quantidade-carrinho="quantidadeCarrinho"
      @logout="fazerLogout"
    />

    <div class="container py-5">
      <div v-if="carregandoUsuario" class="text-center py-5">
        <div class="spinner-border text-dark" role="status"></div>
        <p class="mt-3 text-muted">A carregar os seus dados...</p>
      </div>

      <div v-else class="row g-5 text-start">
        <div class="col-lg-4">
          <PerfilDadosForm
            :usuario-id="usuarioId"
            :dados-usuario="dadosUsuario"
            :is-admin="isAdmin"
            @salvo="onDadosSalvos"
          />
        </div>

        <div class="col-lg-8">
          <PerfilPedidos
            :usuario-id="usuarioId"
            @ir-pagamento="irParaPagamento"
          />
        </div>
      </div>
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

    <Footer />
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";

import Navbar from "../../components/layout/Navbar.vue";
import CarrinhoSidebar from "../../components/layout/CarrinhoSidebar.vue";
import CarrinhoFlutuante from "../../components/layout/CarrinhoFlutuante.vue";
import Footer from "../../components/layout/Footer.vue";
import PerfilDadosForm from "../../components/perfil/PerfilDadosForm.vue";
import PerfilPedidos from "../../components/perfil/PerfilPedidos.vue";

import { useAuth } from "../../composables/useAuth.js";
import { useCarrinho } from "../../composables/useCarrinho.js";
import api from "../../services/api.js";

const router = useRouter();

const { estaLogado, isAdmin, nomeUsuario, usuarioId, carregarIdentidade, fazerLogout } = useAuth();
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


const carregandoUsuario = ref(true);
const dadosUsuario = ref({ nome: "", email: "" });

const obterDadosUsuario = async () => {
  try {
    carregandoUsuario.value = true;
    const resposta = await api.get(`/usuarios/${usuarioId.value}`);
    dadosUsuario.value = resposta.data;

    if (resposta.data.nome) {
      const primeiroNome = resposta.data.nome.split(" ")[0];
      nomeUsuario.value = primeiroNome;
      localStorage.setItem("nome_usuario_vestebem", primeiroNome);
    }
  } catch (err) {
    console.error("Erro ao carregar dados do usuário:", err);
  } finally {
    carregandoUsuario.value = false;
  }
};

const onDadosSalvos = (novosDados) => {
  dadosUsuario.value = novosDados;
  if (novosDados.nome) {
    const primeiroNome = novosDados.nome.split(" ")[0];
    nomeUsuario.value = primeiroNome;
    localStorage.setItem("nome_usuario_vestebem", primeiroNome);
  }
};

const irParaPagamento = (pedidoId) => {
  router.push({ path: "/pagamento", query: { pedidoId } });
};

onMounted(async () => {
  const autenticado = carregarIdentidade();
  if (!autenticado) {
    router.push("/login");
    return;
  }

  await Promise.all([obterDadosUsuario(), carregarCarrinho()]);
});
</script>

<style scoped>
.perfil-wrapper {
  min-height: 100vh;
  background-color: var(--surface-color, #f8fafc);
}
</style>
