<template>
  <div class="row mt-5 pt-5 border-top text-start">
    <div class="col-12">
      <h3 class="fw-bold mb-4">Avaliações dos Clientes</h3>

      <div v-if="!avaliacoes?.length" class="text-muted py-4">
        <i class="ph ph-chat-centered-dots fs-1 mb-2"></i>
        <p>Este produto ainda não possui avaliações. Seja o primeiro a avaliar!</p>
      </div>

      <div v-else class="d-flex flex-column gap-4">
        <div
          v-for="avaliacao in avaliacoes"
          :key="avaliacao.id"
          class="p-4 rounded-4 bg-light shadow-sm"
        >
          <div class="d-flex justify-content-between align-items-start mb-2">
            <div>
              <h6 class="fw-bold mb-1">{{ avaliacao.nomeUsuario }}</h6>
              <span class="text-muted small">{{ formatarData(avaliacao.dataAvaliacao) }}</span>
            </div>

            <!-- Estrelas individuais -->
            <div class="d-flex align-items-center" style="gap: 2px;">
              <span
                v-for="star in 5"
                :key="star"
                style="position: relative; display: inline-flex; width: 16px; height: 16px;"
              >
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 256 256" style="width:16px;height:16px;display:block;">
                  <path d="M128,22.9l27.5,55.7l61.5,8.9l-44.5,43.4l10.5,61.3L128,163.8l-55,28.9l10.5-61.3L39,87.5l61.5-8.9L128,22.9z" fill="#e0e0e0" />
                </svg>
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  viewBox="0 0 256 256"
                  style="position:absolute;top:0;left:0;width:16px;height:16px;display:block;"
                  :style="{ clipPath: `inset(0 ${100 - preencherEstrela(avaliacao.nota, star)}% 0 0)` }"
                >
                  <path d="M128,22.9l27.5,55.7l61.5,8.9l-44.5,43.4l10.5,61.3L128,163.8l-55,28.9l10.5-61.3L39,87.5l61.5-8.9L128,22.9z" fill="#ffc107" />
                </svg>
              </span>
            </div>
          </div>

          <p class="text-secondary mb-0 mt-2">{{ avaliacao.comentario }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { formatarData } from "../../utils/date.js";

defineProps({
  avaliacoes: {
    type: Array,
    default: () => [],
  },
});

const preencherEstrela = (nota, posicao) => {
  if (nota >= posicao) return 100;
  if (nota > posicao - 1) return Math.round((nota - (posicao - 1)) * 100);
  return 0;
};
</script>
