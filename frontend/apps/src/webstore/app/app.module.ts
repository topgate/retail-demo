import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MaterialModule } from './shared/material.module';

import { AppComponent } from './app.component';
import { StoreService } from './shared/store.service';
import { StoreItemComponent } from './store-item/store-item.component';
import { StoreOrderedMessageComponent } from './store-ordered-message/store-ordered-message.component';

@NgModule({
  declarations: [
    AppComponent,
    StoreItemComponent,
    StoreOrderedMessageComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule,
    BrowserAnimationsModule,
    FlexLayoutModule,
    MaterialModule
  ],
  providers: [
    StoreService
  ],
  entryComponents: [
    StoreOrderedMessageComponent
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
