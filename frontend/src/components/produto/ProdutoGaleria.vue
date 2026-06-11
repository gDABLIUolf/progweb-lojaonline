<template>
  <div class="galeria-container">
    <!-- Imagem Principal -->
    <div class="imagem-principal-wrapper position-relative rounded-4 overflow-hidden shadow-sm bg-light">
      <img
        v-if="imagemPrincipalUrl"
        :src="imagemPrincipalUrl"
        :alt="produto.nome"
        class="imagem-principal"
      />
      <div
        v-else
        class="imagem-principal d-flex align-items-center justify-content-center text-muted"
      >
        Sem Imagem
      </div>

      <!-- Setas Prev/Next -->
      <template v-if="produto.imagensIds?.length > 1">
        <button class="btn-seta seta-esquerda" @click="anterior" title="Anterior">
          <i class="ph ph-caret-left"></i>
        </button>
        <button class="btn-seta seta-direita" @click="proxima" title="Próxima">
          <i class="ph ph-caret-right"></i>
        </button>
      </template>
    </div>

    <!-- Miniaturas -->
    <div
      v-if="produto.imagensIds?.length > 1"
      class="miniaturas-row d-flex gap-2 mt-3 overflow-x-auto"
    >
      <button
        v-for="(imgId, idx) in produto.imagensIds"
        :key="imgId"
        class="miniatura-btn rounded-3 overflow-hidden"
        :class="{ 'miniatura-ativa': indiceAtivo === idx }"
        @click="indiceAtivo = idx"
      >
        <img
          :src="`http://localhost:8080/api/produtos/imagem/${imgId}`"
          :alt="`Miniatura ${idx + 1}`"
          class="miniatura-img"
        />
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";

const props = defineProps({
  produto: {
    type: Object,
    required: true,
  },
});

const indiceAtivo = ref(0);

const imagemPrincipalUrl = computed(() => {
  if (!props.produto?.imagensIds?.length) return null;
  const id = props.produto.imagensIds[indiceAtivo.value];
  return `http://localhost:8080/api/produtos/imagem/${id}`;
});

const anterior = () => {
  const len = props.produto.imagensIds.length;
  indiceAtivo.value = (indiceAtivo.value - 1 + len) % len;
};

const proxima = () => {
  const len = props.produto.imagensIds.length;
  indiceAtivo.value = (indiceAtivo.value + 1) % len;
};
</script>

<style scoped>
.galeria-container {
  display: flex;
  flex-direction: column;
}

.imagem-principal-wrapper {
  aspect-ratio: 1/1;
  width: 100%;
}

.imagem-principal {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.btn-seta {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background: rgba(255, 255, 255, 0.85);
  border: none;
  width: 44px;
  height: 44px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.25rem;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.2s ease;
  z-index: 10;
}

.btn-seta:hover {
  background: white;
  transform: translateY(-50%) scale(1.05);
}

.seta-esquerda { left: 1.5rem; }
.seta-direita  { right: 1.5rem; }

.miniatura-btn {
  width: 80px;
  height: 80px;
  flex-shrink: 0;
  border: 2px solid transparent;
  background: transparent;
  padding: 0;
  cursor: pointer;
  transition: all 0.2s ease;
}

.miniatura-ativa {
  border-color: var(--primary-color);
}

.miniatura-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
</style>
