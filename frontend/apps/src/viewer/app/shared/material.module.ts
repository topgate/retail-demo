import { NgModule } from '@angular/core';
import {
  MdToolbarModule,
  MdCardModule,
  MdIconModule,
  MdMenuModule,
  MdButtonModule
} from '@angular/material';

const materialModules = [
  MdToolbarModule,
  MdCardModule,
  MdIconModule,
  MdMenuModule,
  MdButtonModule
];

@NgModule({
  imports: materialModules,
  exports: materialModules
})
export class MaterialModule { }
