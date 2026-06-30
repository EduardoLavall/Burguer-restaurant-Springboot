export function formatarMoeda(valor: number) {
  return valor.toLocaleString("pt-BR", {
    style: "currency",
    currency: "BRL",
  });
}

export function formatarData(valor: string) {
  return new Date(valor).toLocaleString("pt-BR", {
    dateStyle: "short",
    timeStyle: "short",
  });
}

export function formatarStatus(valor: string) {
  return valor.replace(/_/g, " ");
}

export function formatarCategoria(valor: string) {
  return valor.replace(/_/g, " ");
}
