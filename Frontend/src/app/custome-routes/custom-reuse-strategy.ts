import { RouteReuseStrategy, DetachedRouteHandle, ActivatedRouteSnapshot } from '@angular/router';

export class CustomReuseStrategy implements RouteReuseStrategy {
  private storedRoutes: { [key: string]: DetachedRouteHandle } = {};

  shouldDetach(route: ActivatedRouteSnapshot): boolean {
    return route.routeConfig?.path === ''; 
  }
  
  store(route: ActivatedRouteSnapshot, handle: DetachedRouteHandle): void {
    this.storedRoutes[route.routeConfig!.path!] = handle;
  }
  
  shouldAttach(route: ActivatedRouteSnapshot): boolean {
    return !!this.storedRoutes[route.routeConfig!.path!];
  }
  
  retrieve(route: ActivatedRouteSnapshot): DetachedRouteHandle | null {
    return this.storedRoutes[route.routeConfig!.path!] || null;
  }
  
  shouldReuseRoute(future: ActivatedRouteSnapshot, curr: ActivatedRouteSnapshot): boolean {
    return future.routeConfig === curr.routeConfig;
  }
  
}
