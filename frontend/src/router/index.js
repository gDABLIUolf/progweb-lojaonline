// src/router/index.js
import { createRouter, createWebHistory } from "vue-router";
import LoginView from "../views/auth/LoginView.vue";
import HomeView from "../views/shop/HomeView.vue";
import CadastroView from "../views/auth/CadastroView.vue";
import CarrinhoView from "../views/shop/CarrinhoView.vue";
import ProdutoView from "../views/shop/ProdutoView.vue";

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: "/", name: "home", component: HomeView },
    { path: "/login", name: "login", component: LoginView },
    { path: "/cadastro", name: "cadastro", component: CadastroView },
    { path: "/carrinho", name: "carrinho", component: CarrinhoView },
    { path: "/produto/:id", name: "produto-detalhes", component: ProdutoView },
  ],
});

export default router;