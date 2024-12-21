import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { LoginComponent } from './Components/login/login.component';
import { DashboardComponent } from './Components/dashboard/dashboard.component';
import { AppRoutingModule } from './app-routing.module';
import {FormsModule} from "@angular/forms";
import {HttpClientModule} from "@angular/common/http";
import { UsersComponent } from './Components/users/users.component';
import { MainLayoutComponent } from './Components/main-layout/main-layout.component';
import { RegisterComponent } from './Components/register/register.component';
import { VerifyComponent } from './Components/verify/verify.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import {MatTableModule} from "@angular/material/table";
import {MatInputModule} from "@angular/material/input";
import {MatSelectModule} from "@angular/material/select";
import {MatDialogModule} from "@angular/material/dialog";
import { ProductsComponent } from './Components/products/products.component';
import { WarehousesComponent } from './Components/warehouses/warehouses.component';
import { CategoriesComponent } from './Components/categories/categories.component';
import { SuppliersComponent } from './Components/suppliers/suppliers.component';
import { MovementsComponent } from './Components/movements/movements.component';
import {MatPaginatorModule} from "@angular/material/paginator";
import {MatNativeDateModule} from "@angular/material/core";
import {MatDatepickerModule} from "@angular/material/datepicker";
import {MatSnackBarModule} from "@angular/material/snack-bar";
import {MatCheckboxModule} from "@angular/material/checkbox";

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    DashboardComponent,
    UsersComponent,
    MainLayoutComponent,
    RegisterComponent,
    VerifyComponent,
    ProductsComponent,
    WarehousesComponent,
    CategoriesComponent,
    SuppliersComponent,
    MovementsComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    BrowserAnimationsModule,
    MatIconModule,
    MatButtonModule,
    MatTableModule,
    MatInputModule,
    MatSelectModule,
    MatDialogModule,
    MatPaginatorModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatSnackBarModule,
    MatCheckboxModule,
  ],
  providers: [MatDatepickerModule],
  bootstrap: [AppComponent]
})
export class AppModule { }
