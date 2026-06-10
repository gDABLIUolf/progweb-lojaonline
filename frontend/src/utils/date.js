/**
 * Formata uma data string para o padrão brasileiro (pt-BR) utilizando o fuso horário UTC-4 (America/Manaus) por padrão.
 * 
 * Se a string não contiver informações de timezone (Z ou +/-offset), assume que o horário fornecido está em UTC-4.
 * 
 * @param {string} dataStr - String de data em formato ISO 8601 (ex: "2026-06-10T18:57:17")
 * @param {boolean} includeTime - Se deve incluir a hora e o minuto na formatação
 * @returns {string} - Data formatada (ex: "10/06/2026 18:57" ou "10/06/2026")
 */
export const formatarData = (dataStr, includeTime = false) => {
  if (!dataStr) return "";
  try {
    let formattedStr = dataStr;
    // Se a data vier sem indicação de fuso horário (ex: "2026-06-10T18:57:17"),
    // assume que ela está no fuso UTC-4 (GMT-4).
    if (!dataStr.endsWith("Z") && !/[+-]\d{2}:\d{2}$/.test(dataStr)) {
      if (dataStr.includes("T")) {
        formattedStr = dataStr + "-04:00";
      } else {
        formattedStr = dataStr + "T00:00:00-04:00";
      }
    }
    const dateObj = new Date(formattedStr);
    const options = {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      timeZone: "America/Manaus"
    };
    if (includeTime) {
      options.hour = "2-digit";
      options.minute = "2-digit";
    }
    return dateObj.toLocaleDateString("pt-BR", options);
  } catch (e) {
    console.error("Erro ao formatar data:", e);
    return dataStr;
  }
};
