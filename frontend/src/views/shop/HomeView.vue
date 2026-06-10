<template>
  <div class="home-wrapper">
    <Navbar
      :esta-logado="estaLogado"
      :is-admin="isAdmin"
      :nome-usuario="nomeUsuario"
      :quantidade-carrinho="2"
      @logout="fazerLogout"
    />

    <HeroSection />

    <AdminPanel
      v-if="isAdmin"
      @nova-categoria="modalCategoriaAberta = true"
      @novo-produto="modalProdutoAberta = true"
    />

    <CategoriaModal
      v-model:open="modalCategoriaAberta"
      @categoria-salva="carregarCategorias"
    />

    <ProdutoModal
      v-model:open="modalProdutoAberta"
      :categorias="categorias"
      @produto-salvo="carregarCategorias"
    />

    <CategoriaCarrossel :categorias="categorias" />

    <ProdutoList :produtos="produtos" />
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";

import Navbar from "../../components/layout/Navbar.vue";
import HeroSection from "../../components/home/home.vue";

import AdminPanel from "../../components/admin/AdminPanel.vue";
import CategoriaModal from "../../components/modal/CategoriaModal.vue";
import ProdutoModal from "../../components/modal/ProductModal.vue";

import CategoriaList from "../../components/categoria/CategoriaList.vue";
import CategoriaCarrossel from "../../components/categoria/CategoriaCarrossel.vue";

import ProdutoList from "../../components/produto/ProdutoList.vue";

import api from "../../services/api.js";

const router = useRouter();
const estaLogado = ref(false);
const isAdmin = ref(false);
const nomeUsuario = ref("");

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

  if (dadosToken.sub) {
    nomeUsuario.value = dadosToken.sub.split("@")[0];
  }

  const permissao = dadosToken.role || "";

  if (permissao.toUpperCase() === "ADMIN") {
    isAdmin.value = true;
  } else {
    isAdmin.value = false;
  }
};
const fazerLogout = () => {
  localStorage.removeItem("token_vestebem");

  estaLogado.value = false;
  isAdmin.value = false;
  nomeUsuario.value = "";

  router.go();
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

const modalCategoriaAberta = ref(false);
const modalProdutoAberta = ref(false);

const produtos = ref([]);

const carregarProdutos = async () => {
  try {
    const response = await api.get("/produtos");
    produtos.value = response.data;
  } catch (error) {
    console.error("Erro ao carregar produtos:", error);
  }
};

onMounted(async () => {
  carregarUsuario();

  await Promise.all([carregarCategorias(), carregarProdutos()]);
});
</script>

<style scoped>
.home-wrapper {
  min-height: 100vh;
  background-color: var(--bg-color);
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

@media (max-width: 992px) {
  .home-wrapper {
    overflow-x: hidden;
  }
}
</style>
