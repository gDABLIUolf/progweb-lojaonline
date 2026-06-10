<template>
  <section class="produtos py-5 bg-light">
    <div class="container px-4">
      <div class="d-flex flex-column align-items-center mb-5 gap-3">
        <div class="d-flex align-items-center justify-content-center gap-3 w-100 flex-wrap position-relative">
          <h2 class="titulo-secao m-0">Todos os Produtos</h2>
          <button
            @click="alternarBusca"
            class="btn btn-search-toggle"
            :class="{ 'active': buscaExpandida }"
            title="Pesquisar produtos"
          >
            <i class="ph ph-magnifying-glass fs-4"></i>
          </button>
        </div>

        <!-- Barra de busca expansível -->
        <div v-if="buscaExpandida" class="search-box-container fade-in">
          <div class="search-input-wrapper">
            <input
              type="text"
              v-model="filtroNome"
              @input="emitirFiltro"
              placeholder="Pesquisar por nome..."
              class="form-control input-busca"
              ref="inputBusca"
            />
            <button
              @click="toggleFiltroCategorias"
              class="btn btn-filtro"
              :class="{ 'active': showCategoriasMenu }"
            >
              <i class="ph ph-funnel me-1"></i>
              Filtro
              <span v-if="categoriasSelecionadas.length > 0" class="badge bg-dark ms-1">
                {{ categoriasSelecionadas.length }}
              </span>
            </button>
          </div>

          <!-- Painel flutuante de categorias (dropdown) -->
          <div v-if="showCategoriasMenu" class="categorias-dropdown-panel shadow">
            <h6 class="fw-bold mb-3 border-bottom pb-2">Filtrar por Categorias</h6>
            <div class="categorias-grid">
              <div v-for="cat in categorias" :key="cat.id" class="form-check py-1">
                <input
                  class="form-check-input custom-checkbox"
                  type="checkbox"
                  :value="cat.id"
                  :id="'cat-' + cat.id"
                  v-model="categoriasSelecionadas"
                  @change="emitirFiltro"
                />
                <label class="form-check-label ms-2" :for="'cat-' + cat.id">
                  {{ cat.nome }}
                </label>
              </div>
            </div>
            <div class="d-flex justify-content-between mt-3 pt-2 border-top">
              <button
                @click="limparFiltros"
                class="btn btn-sm btn-link text-muted p-0 text-decoration-none"
              >
                Limpar filtros
              </button>
              <button
                @click="showCategoriasMenu = false"
                class="btn btn-sm btn-dark px-3 rounded-pill"
              >
                Aplicar
              </button>
            </div>
          </div>
        </div>
      </div>

      <div v-if="produtos.length === 0" class="text-center text-muted py-5">
        <i class="ph ph-package fs-1 mb-3"></i>
        <p>Nenhum produto encontrado.</p>
      </div>

      <div v-else class="produtos-wrapper-container">
        <div class="row g-4">
          <div
            v-for="(produto, index) in (mostrarTodos || isCatalogoPage ? produtos : produtos.slice(0, 21))"
            :key="produto.id"
            :class="[
              isCatalogoPage ? 'col-12 col-md-6 col-lg-4 col-xl-3' : 'col-12 col-md-6 col-lg-4',
              { 'item-ofuscado': !mostrarTodos && !isCatalogoPage && produtos.length > 18 && index >= 18 }
            ]"
            :data-index="index"
          >
            <ProdutoCard
              :produto="produto"
              @adicionar-carrinho="$emit('adicionar-carrinho', $event)"
            />
          </div>
        </div>

        <!-- Botão "Mostrar Todos" sobrepondo a linha cortada (redireciona para o catálogo) -->
        <div v-if="!mostrarTodos && !isCatalogoPage && produtos.length > 18" class="botao-ver-mais-wrapper">
          <button @click="irParaCatalogo" class="btn btn-mostrar-todos shadow-lg">
            <i class="ph ph-squares-four me-2"></i>
            Mostrar Todos ({{ produtos.length }} produtos)
          </button>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, watch, onMounted, onBeforeUnmount } from "vue";
import { useRouter } from "vue-router";
import ProdutoCard from "./ProdutoCard.vue";

const props = defineProps({
  produtos: {
    type: Array,
    default: () => [],
  },
  categorias: {
    type: Array,
    default: () => [],
  },
  filtroCategoriasInicial: {
    type: Array,
    default: () => [],
  },
  filtroNomeInicial: {
    type: String,
    default: "",
  },
  isCatalogoPage: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(["adicionar-carrinho", "filtrar"]);

const router = useRouter();

const irParaCatalogo = () => {
  router.push("/produtos");
};

const filtroNome = ref(props.filtroNomeInicial);
const categoriasSelecionadas = ref([...props.filtroCategoriasInicial]);
const buscaExpandida = ref(props.filtroCategoriasInicial.length > 0 || !!props.filtroNomeInicial);
const showCategoriasMenu = ref(props.filtroCategoriasInicial.length > 0);

const inputBusca = ref(null);
const mostrarTodos = ref(false);

watch(() => props.produtos, () => {
  mostrarTodos.value = false;
});

watch(() => props.filtroCategoriasInicial, (newVal) => {
  categoriasSelecionadas.value = [...newVal];
  if (newVal.length > 0) {
    buscaExpandida.value = true;
    showCategoriasMenu.value = true;
  }
}, { deep: true });

watch(() => props.filtroNomeInicial, (newVal) => {
  filtroNome.value = newVal;
  if (newVal) {
    buscaExpandida.value = true;
  }
});

const alternarBusca = () => {
  buscaExpandida.value = !buscaExpandida.value;
  if (!buscaExpandida.value) {
    filtroNome.value = "";
    categoriasSelecionadas.value = [];
    showCategoriasMenu.value = false;
    emitirFiltro();
  } else {
    setTimeout(() => {
      if (inputBusca.value) {
        inputBusca.value.focus();
      }
    }, 100);
  }
};

const toggleFiltroCategorias = () => {
  showCategoriasMenu.value = !showCategoriasMenu.value;
};

const limparFiltros = () => {
  filtroNome.value = "";
  categoriasSelecionadas.value = [];
  emitirFiltro();
};

const emitirFiltro = () => {
  emit("filtrar", {
    nome: filtroNome.value,
    categoriasIds: categoriasSelecionadas.value,
  });
};

const fecharDropdownSeFora = (event) => {
  const dropdownEl = document.querySelector(".categorias-dropdown-panel");
  const buttonEl = document.querySelector(".btn-filtro");
  if (
    showCategoriasMenu.value &&
    dropdownEl &&
    !dropdownEl.contains(event.target) &&
    buttonEl &&
    !buttonEl.contains(event.target)
  ) {
    showCategoriasMenu.value = false;
  }
};

onMounted(() => {
  document.addEventListener("click", fecharDropdownSeFora);
});

onBeforeUnmount(() => {
  document.removeEventListener("click", fecharDropdownSeFora);
});
</script>

<style scoped>
.titulo-secao {
  font-size: 2.2rem;
  font-weight: 600;
  color: #111;
}

.btn-search-toggle {
  background: white;
  border: 1px solid #dee2e6;
  border-radius: 50%;
  width: 45px;
  height: 45px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  color: #333;
}

.btn-search-toggle:hover {
  background: #f8f9fa;
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.btn-search-toggle.active {
  background: #111;
  color: white;
  border-color: #111;
}

.search-box-container {
  width: 100%;
  max-width: 500px;
  position: relative;
  margin-top: 10px;
  z-index: 1050;
}

.search-input-wrapper {
  display: flex;
  align-items: center;
  background: white;
  border: 1px solid #dee2e6;
  border-radius: 30px;
  padding: 5px 5px 5px 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
}

.search-input-wrapper:focus-within {
  border-color: #111;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
}

.input-busca {
  border: none;
  background: transparent;
  padding: 8px 0;
  outline: none;
  width: 100%;
  font-size: 0.95rem;
}

.input-busca:focus {
  box-shadow: none;
  border-color: transparent;
  background: transparent;
}

.btn-filtro {
  background: #f1f3f5;
  border: none;
  border-radius: 20px;
  padding: 8px 16px;
  font-weight: 500;
  font-size: 0.9rem;
  color: #495057;
  display: flex;
  align-items: center;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.btn-filtro:hover {
  background: #e9ecef;
  color: #212529;
}

.btn-filtro.active {
  background: #111;
  color: white;
}

.categorias-dropdown-panel {
  position: absolute;
  top: 100%;
  right: 0;
  left: 0;
  margin-top: 10px;
  background: white;
  border: 1px solid #dee2e6;
  border-radius: 16px;
  padding: 20px;
  z-index: 1100;
  text-align: left;
  animation: slideDown 0.3s cubic-bezier(0.25, 0.8, 0.25, 1) forwards;
}

.categorias-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px 20px;
  max-height: 200px;
  overflow-y: auto;
  padding: 4px;
}

.custom-checkbox {
  cursor: pointer;
}

.custom-checkbox:checked {
  background-color: #111;
  border-color: #111;
}

.form-check-label {
  cursor: pointer;
  font-size: 0.95rem;
  color: #495057;
  user-select: none;
}

.form-check-input:focus {
  border-color: #111;
  box-shadow: 0 0 0 0.25rem rgba(17, 17, 17, 0.25);
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.fade-in {
  animation: fadeIn 0.3s cubic-bezier(0.25, 0.8, 0.25, 1) forwards;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(5px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.produtos-wrapper-container {
  position: relative;
}

.item-ofuscado {
  max-height: 150px;
  overflow: hidden;
  position: relative;
  pointer-events: none;
  opacity: 0.4;
  transition: all 0.5s ease;
}

/* Efeito de fade do topo (transparente) para o rodapé (cor de fundo) */
.item-ofuscado::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to bottom, rgba(248, 249, 250, 0) 0%, rgba(248, 249, 250, 0.7) 50%, rgba(248, 249, 250, 1) 100%);
  pointer-events: none;
  z-index: 2;
}

/* Regras responsivas para exibir apenas a linha cortada correspondente na tela */
@media (max-width: 767px) {
  /* Mobile (1 coluna): mostra apenas o índice 18 e oculta 19 e 20 */
  .item-ofuscado[data-index="19"],
  .item-ofuscado[data-index="20"] {
    display: none;
  }
}

@media (min-width: 768px) and (max-width: 991px) {
  /* Tablet (2 colunas): mostra índices 18 e 19, oculta o 20 */
  .item-ofuscado[data-index="20"] {
    display: none;
  }
}

.botao-ver-mais-wrapper {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: -65px;
  padding-bottom: 20px;
  z-index: 5;
}

.btn-mostrar-todos {
  background: #111;
  color: white;
  border: none;
  border-radius: 30px;
  padding: 14px 32px;
  font-weight: 600;
  font-size: 1rem;
  letter-spacing: -0.01em;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.btn-mostrar-todos:hover {
  background: #222;
  transform: translateY(-3px);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.2);
}

.btn-mostrar-todos:active {
  transform: translateY(-1px);
}
</style>
