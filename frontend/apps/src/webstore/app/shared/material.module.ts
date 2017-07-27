import { NgModule } from '@angular/core';
import {
  MdToolbarModule,
  MdCardModule,
  MdIconModule,
  MdMenuModule,
  MdButtonModule,
  MdSnackBarModule
} from '@angular/material';

const materialModules = [
  MdToolbarModule,
  MdCardModule,
  MdIconModule,
  MdMenuModule,
  MdButtonModule,
  MdSnackBarModule
];

@NgModule({
  imports: materialModules,
  exports: materialModules
})
export class MaterialModule { }
