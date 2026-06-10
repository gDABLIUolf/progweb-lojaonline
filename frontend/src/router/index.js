// src/router/index.js
import { createRouter, createWebHistory } from "vue-router";
import LoginView from "../views/auth/LoginView.vue";
import HomeView from "../views/shop/HomeView.vue";
import CadastroView from "../views/auth/CadastroView.vue"; // Adicione essa linha

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: "/", name: "home", component: HomeView },
    { path: "/login", name: "login", component: LoginView },
    { path: "/cadastro", name: "cadastro", component: CadastroView }, // Adicione essa linha
  ],
});

export default router;