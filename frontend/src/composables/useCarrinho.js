import { ref, computed } from "vue";
import api from "../services/api.js";
import { showToast } from "../services/toast.js";


export function useCarrinho(usuarioId, estaLogado) {
  const itensCarrinho = ref([]);
  const subtotalCarrinho = ref(0);
  const sidebarAberta = ref(false);
  const modalLoginAberto = ref(false);

  const quantidadeCarrinho = computed(() => itensCarrinho.value.length);

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


  const adicionarAoCarrinho = async (produtoId, quantidade = 1) => {
    if (!estaLogado.value) {
      modalLoginAberto.value = true;
      return;
    }

    try {
      const resposta = await api.post(
        `/carrinhos/${usuarioId.value}/adicionar`,
        { produtoId, quantidade },
      );
      itensCarrinho.value = resposta.data.itens || [];
      subtotalCarrinho.value = resposta.data.subtotal || 0;
      sidebarAberta.value = true;
    } catch (error) {
      const rawData = error.response?.data;
      const msg =
        typeof rawData === "string"
          ? rawData
          : rawData?.message || "Erro ao adicionar ao carrinho.";
      showToast(msg, "error");
    }
  };


  const adicionarItemSidebar = async (produtoId) => {
    try {
      const resposta = await api.post(
        `/carrinhos/${usuarioId.value}/adicionar`,
        { produtoId, quantidade: 1 },
      );
      itensCarrinho.value = resposta.data.itens || [];
      subtotalCarrinho.value = resposta.data.subtotal || 0;
    } catch (error) {
      const rawData = error.response?.data;
      const msg =
        typeof rawData === "string"
          ? rawData
          : rawData?.message || "Erro ao adicionar item.";
      showToast(msg, "error");
    }
  };


  const removerItemSidebar = async (produtoId) => {
    try {
      const resposta = await api.delete(
        `/carrinhos/${usuarioId.value}/remover/${produtoId}`,
      );
      itensCarrinho.value = resposta.data.itens || [];
      subtotalCarrinho.value = resposta.data.subtotal || 0;
    } catch (error) {
      const rawData = error.response?.data;
      const msg =
        typeof rawData === "string"
          ? rawData
          : rawData?.message || "Erro ao remover item.";
      showToast(msg, "error");
    }
  };

  return {
    itensCarrinho,
    subtotalCarrinho,
    sidebarAberta,
    modalLoginAberto,
    quantidadeCarrinho,
    carregarCarrinho,
    adicionarAoCarrinho,
    adicionarItemSidebar,
    removerItemSidebar,
  };
}
