import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import { Observable } from 'rxjs';
import {Product} from "../Model/product.model";
import {Inventory} from "../Model/inventory.model";
import {Warehouse} from "../Model/warehouse.model";
import {Supplier} from "../Model/supplier.model";

export interface InventoryResponse {
  content: Inventory[];
  totalElements: number;
}

export interface WareHouseResponse {
  content: Warehouse[];
  totalElements: number;
}

export interface SupplierResponse {
  content: Supplier[];
  totalElements: number;
}

export interface ProductResponse {
  content: Product[];
  totalElements: number;
}

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private apiUrl = 'http://localhost:8080/api/v1/inventory';

  constructor(private http: HttpClient) {}

  //Inventory
  addProductToInventory(warehouseId: number, productId: number, quantity: number): Observable<any> {
    const token = sessionStorage.getItem('authToken');
    if (!token) {
      throw new Error('Token no encontrado');
    }

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    const params = new HttpParams()
      .set('warehouseId', warehouseId.toString())
      .set('productId', productId.toString())
      .set('quantity', quantity.toString());

    return this.http.post<any>(`${this.apiUrl}/addProductToInventory`, {}, { headers, params });
  }

  //Supplier
  getSuppliers(
    page: number,
    size: number
  ): Observable<SupplierResponse> {
    const token = sessionStorage.getItem('authToken');

    if (!token) {
      throw new Error('Token no encontrado');
    }

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<any>('http://localhost:8080/api/v1/supplier/getAllSuppliers', { headers, params });
  }

  getAllSuppliers(page: number, size: number): Observable<any> {
    const token = sessionStorage.getItem('authToken');
    if (!token) {
      throw new Error('Token no encontrado');
    }
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<any[]>('http://localhost:8080/api/v1/supplier/getAllSuppliers', { headers });
  }

  registerSupplier(supplierDate: any): Observable<any> {
    const token = sessionStorage.getItem('authToken');
    if (!token) {
      throw new Error('Token no encontrado');
    }
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return this.http.post<any>('http://localhost:8080/api/v1/supplier/addSupplier', supplierDate, { headers });
  }

  deactivateSupplier(idSupplier: number): Observable<any> {
    const token = sessionStorage.getItem('authToken');
    if (!token) {
      throw new Error('Token no encontrado');
    }
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return this.http.patch<any>(`http://localhost:8080/api/v1/supplier/${idSupplier}/deactivate`, {}, { headers });
  }

  updateSupplier(idSupplier: number, updatedSupplier: any): Observable<any> {
    const token = sessionStorage.getItem('authToken');

    if (!token) {
      throw new Error('Token no encontrado');
    }

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return this.http.put<any>(`http://localhost:8080/api/v1/supplier/update/${idSupplier}`, updatedSupplier, { headers });
  }

  //Category
  getAllCategories(page: number, size: number): Observable<any> {
    const token = sessionStorage.getItem('authToken');
    if (!token) {
      throw new Error('Token no encontrado');
    }
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<any[]>('http://localhost:8080/api/v1/product-category/getAllCategories', { headers });
  }

  registerCategory(categoryDate: any): Observable<any> {
    const token = sessionStorage.getItem('authToken');
    if (!token) {
      throw new Error('Token no encontrado');
    }
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return this.http.post<any>('http://localhost:8080/api/v1/product-category/addCategory', categoryDate, { headers });
  }

  updateCategory(idCategory: number, updatedCategory: any): Observable<any> {
    const token = sessionStorage.getItem('authToken');

    if (!token) {
      throw new Error('Token no encontrado');
    }

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return this.http.put<any>(`http://localhost:8080/api/v1/product-category/update/${idCategory}`, updatedCategory, { headers });
  }

  deactivateCategory(idCategory: number): Observable<any> {
    const token = sessionStorage.getItem('authToken');
    if (!token) {
      throw new Error('Token no encontrado');
    }
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return this.http.patch<any>(`http://localhost:8080/api/v1/product-category/${idCategory}/deactivate`, {}, { headers });
  }

  //Products
  getProducts(
    page: number,
    size: number
  ): Observable<InventoryResponse> {
    const token = sessionStorage.getItem('authToken');

    if (!token) {
      throw new Error('Token no encontrado');
    }

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<any>(`${this.apiUrl}/getInventory`, { headers, params });
  }

  getAllProducts(page: number, size: number): Observable<any> {
    const token = sessionStorage.getItem('authToken');

    if (!token) {
      throw new Error('Token no encontrado');
    }

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<any[]>('http://localhost:8080/api/v1/products/getAllProducts', { headers, params });
  }

  getProductsFilter(
    categoryId: number | null,
    supplierId: number | null,
    minimumPrice: number | null,
    maxPrice: number | null,
    page: number,
    size: number
  ): Observable<ProductResponse> {
    const token = sessionStorage.getItem('authToken');
    if (!token) {
      throw new Error('Token no encontrado');
    }
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (categoryId !== null && categoryId !== undefined) {
      params = params.set('categoryId', categoryId.toString());
    }

    if (supplierId !== null && supplierId !== undefined) {
      params = params.set('supplierId', supplierId.toString());
    }

    if (minimumPrice !== null && minimumPrice !== undefined) {
      params = params.set('minimumPrice', minimumPrice.toString());
    }

    if (maxPrice !== null && maxPrice !== undefined) {
      params = params.set('maxPrice', maxPrice.toString());
    }

    return this.http.get<ProductResponse>('http://localhost:8080/api/v1/products/filterProducts', { headers, params });
  }

  registerProduct(productData: any): Observable<any> {
    const token = sessionStorage.getItem('authToken');
    if (!token) {
      throw new Error('Token no encontrado');
    }
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return this.http.post<any>('http://localhost:8080/api/v1/products/addProduct', productData, { headers });
  }

  updateProduct(productId: number, updatedProduct: any): Observable<any> {
    const token = sessionStorage.getItem('authToken');

    if (!token) {
      throw new Error('Token no encontrado');
    }

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return this.http.put<any>(`http://localhost:8080/api/v1/products/update/${productId}`, updatedProduct, { headers });
  }

  deactivateProduct(productId: number): Observable<any> {
    const token = sessionStorage.getItem('authToken');
    if (!token) {
      throw new Error('Token no encontrado');
    }

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return this.http.patch<any>(`http://localhost:8080/api/v1/products/${productId}/deactivate`, {}, { headers });
  }

  //Warehouse
  getWarehouses(
    page: number,
    size: number
  ): Observable<WareHouseResponse> {
    const token = sessionStorage.getItem('authToken');

    if (!token) {
      throw new Error('Token no encontrado');
    }

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<any>('http://localhost:8080/api/v1/warehouse/getAllWarehouses', { headers, params });
  }

  getAllWarehouses(page: number, size: number): Observable<any> {
    const token = sessionStorage.getItem('authToken');

    if (!token) {
      throw new Error('Token no encontrado');
    }

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<any[]>('http://localhost:8080/api/v1/warehouse/getAllWarehouses', { headers });
  }

  registerWarehouse(warehouseDate: any): Observable<any> {
    const token = sessionStorage.getItem('authToken');
    if (!token) {
      throw new Error('Token no encontrado');
    }
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return this.http.post<any>('http://localhost:8080/api/v1/warehouse/addWarehouse', warehouseDate, { headers });
  }

  updateWarehouse(idWarehouse: number, updatedWarehouse: any): Observable<any> {
    const token = sessionStorage.getItem('authToken');

    if (!token) {
      throw new Error('Token no encontrado');
    }

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return this.http.put<any>(`http://localhost:8080/api/v1/warehouse/update/${idWarehouse}`, updatedWarehouse, { headers });
  }

  deactivateWarehouse(idWarehouse: number): Observable<any> {
    const token = sessionStorage.getItem('authToken');

    if (!token) {
      throw new Error('Token no encontrado');
    }

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return this.http.patch<any>(`http://localhost:8080/api/v1/warehouse/${idWarehouse}/deactivate`, {}, { headers });
  }
}
