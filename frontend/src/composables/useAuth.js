import { ref } from "vue";
import { useRouter } from "vue-router";
import api from "../services/api.js";


export function useAuth() {
  const router = useRouter();

  const estaLogado = ref(false);
  const isAdmin = ref(false);
  const nomeUsuario = ref("");
  const usuarioId = ref(null);

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


  const carregarUsuario = async () => {
    const token = localStorage.getItem("token_vestebem");
    if (!token) return false;

    estaLogado.value = true;
    const dadosToken = decodificarJWT(token);

    if (!dadosToken) return false;

    usuarioId.value = dadosToken.usuarioId || dadosToken.id || null;

    const permissao = dadosToken.role || "";
    isAdmin.value = permissao.toUpperCase() === "ADMIN";


    const cachedName = localStorage.getItem("nome_usuario_vestebem");
    if (cachedName) {
      nomeUsuario.value = cachedName;
    } else if (dadosToken.sub) {
      nomeUsuario.value = dadosToken.sub.split("@")[0];
    }


    if (usuarioId.value) {
      try {
        const resposta = await api.get(`/usuarios/${usuarioId.value}`);
        const nomeCompleto = resposta.data.nome || "";
        const primeiroNome = nomeCompleto.split(" ")[0];
        nomeUsuario.value = primeiroNome;
        localStorage.setItem("nome_usuario_vestebem", primeiroNome);
      } catch {

      }
    }

    return true;
  };


  const carregarIdentidade = () => {
    const token = localStorage.getItem("token_vestebem");
    if (!token) return false;

    estaLogado.value = true;
    const dadosToken = decodificarJWT(token);

    if (!dadosToken) return false;

    usuarioId.value = dadosToken.usuarioId || dadosToken.id || null;

    const cachedName = localStorage.getItem("nome_usuario_vestebem");
    if (cachedName) {
      nomeUsuario.value = cachedName;
    } else if (dadosToken.sub) {
      nomeUsuario.value = dadosToken.sub.split("@")[0];
    }

    const permissao = dadosToken.role || "";
    isAdmin.value = permissao.toUpperCase() === "ADMIN";

    return !!usuarioId.value;
  };

  const fazerLogout = (redirectTo = "/") => {
    localStorage.removeItem("token_vestebem");
    localStorage.removeItem("nome_usuario_vestebem");

    estaLogado.value = false;
    isAdmin.value = false;
    nomeUsuario.value = "";
    usuarioId.value = null;

    router.push(redirectTo);
  };

  return {
    estaLogado,
    isAdmin,
    nomeUsuario,
    usuarioId,
    carregarUsuario,
    carregarIdentidade,
    fazerLogout,
  };
}
