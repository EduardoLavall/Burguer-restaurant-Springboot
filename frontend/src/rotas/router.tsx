import {
  Link,
  Navigate,
  Outlet,
  createRootRoute,
  createRoute,
  createRouter,
} from "@tanstack/react-router";

import { AdminPedidosPage } from "../paginas/AdminPedidosPage";
import { ClienteCardapioPage } from "../paginas/ClienteCardapioPage";
import { PedidoConfirmacaoPage } from "../paginas/PedidoConfirmacaoPage";
import { ProdutosPage } from "../paginas/ProdutosPage";

function LayoutRotas() {
  return (
    <div className="shell-app">
      <header className="shell-app__topo">
        <div>
          <span className="shell-app__tag">Hamburgueria v1</span>
          <strong>Burguer Restaurant</strong>
          <p>Tablet do cliente e painel do restaurante no mesmo app React.</p>
        </div>

        <nav className="shell-app__nav" aria-label="Navegacao principal">
          <Link to="/cliente" className="shell-app__link">
            Cliente
          </Link>
          <Link to="/admin/produtos" className="shell-app__link">
            Admin Produtos
          </Link>
          <Link to="/admin/pedidos" className="shell-app__link">
            Admin Pedidos
          </Link>
        </nav>
      </header>

      <Outlet />
    </div>
  );
}

const rootRoute = createRootRoute({
  component: LayoutRotas,
});

const homeRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: "/",
  component: () => <Navigate to="/cliente" replace />,
});

const clienteRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: "/cliente",
  component: ClienteCardapioPage,
});

const pedidoConfirmacaoRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: "/cliente/pedido/$pedidoId",
  component: PedidoConfirmacaoPage,
});

const adminRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: "/admin",
  component: () => <Navigate to="/admin/produtos" replace />,
});

const adminProdutosRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: "/admin/produtos",
  component: ProdutosPage,
});

const adminPedidosRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: "/admin/pedidos",
  component: AdminPedidosPage,
});

const routeTree = rootRoute.addChildren([
  homeRoute,
  clienteRoute,
  pedidoConfirmacaoRoute,
  adminRoute,
  adminProdutosRoute,
  adminPedidosRoute,
]);

const router = createRouter({
  routeTree,
  defaultPreload: "intent",
});

export { router };

declare module "@tanstack/react-router" {
  interface Register {
    router: typeof router;
  }
}
