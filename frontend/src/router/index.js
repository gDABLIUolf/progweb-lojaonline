// src/router/index.js
import { createRouter, createWebHistory } from "vue-router";
import LoginView from "../views/auth/LoginView.vue";
import HomeView from "../views/shop/HomeView.vue";
import CadastroView from "../views/auth/CadastroView.vue";
import CarrinhoView from "../views/shop/CarrinhoView.vue";
import ProdutoView from "../views/shop/ProdutoView.vue";
import PerfilView from "../views/shop/PerfilView.vue";
import ProdutosView from "../views/shop/ProdutosView.vue";
import DescontosView from "../views/shop/DescontosView.vue";
import PagamentoView from "../views/shop/PagamentoView.vue";

import AdminView from "../views/admin/AdminView.vue";

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: "/", name: "home", component: HomeView },
    { path: "/login", name: "login", component: LoginView },
    { path: "/cadastro", name: "cadastro", component: CadastroView },
    { path: "/carrinho", name: "carrinho", component: CarrinhoView },
    { path: "/produto/:id", name: "produto-detalhes", component: ProdutoView },
    { path: "/perfil", name: "perfil", component: PerfilView },
    { path: "/produtos", name: "produtos", component: ProdutosView },
    { path: "/descontos", name: "descontos", component: DescontosView },
    { path: "/pagamento", name: "pagamento", component: PagamentoView },
    { path: "/admin", name: "admin", component: AdminView },
  ],
});

router.beforeEach((to, from, next) => {
  if (to.path === "/admin") {
    const token = localStorage.getItem("token_vestebem");
    if (!token) {
      next("/");
      return;
    }
    const decodificarJWT = (t) => {
      try {
        const payload = t.split(".")[1];
        const base64 = payload.replace(/-/g, "+").replace(/_/g, "/");
        const jsonPayload = decodeURIComponent(
          window.atob(base64)
            .split("")
            .map((c) => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2))
            .join("")
        );
        return JSON.parse(jsonPayload);
      } catch {
        return null;
      }
    };
    const dados = decodificarJWT(token);
    if (dados && dados.role && dados.role.toUpperCase() === "ADMIN") {
      next();
    } else {
      next("/");
    }
  } else {
    next();
  }
});

export default router;