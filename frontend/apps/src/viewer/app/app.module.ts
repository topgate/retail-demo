import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { AngularFireModule } from 'angularfire2';
import { AngularFireDatabaseModule } from 'angularfire2/database';
import { MaterialModule } from './shared/material.module';

import { environment } from '../environments/environment';
import { AppComponent } from './app.component';
import { StreamViewerComponent } from './stream-viewer/stream-viewer.component';
import { TrendViewerComponent } from './trend-viewer/trend-viewer.component';
import { StreamDataService } from './shared/stream-data.service';
import { TrendDataService } from './shared/trend-data.service';
import { TrendItemComponent } from './trend-item/trend-item.component';
import { StreamItemComponent } from './stream-item/stream-item.component';

@NgModule({
  declarations: [
    AppComponent,
    StreamViewerComponent,
    TrendViewerComponent,
    TrendItemComponent,
    StreamItemComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    FlexLayoutModule,
    MaterialModule,
    AngularFireModule.initializeApp(environment.firebase),
    AngularFireDatabaseModule,
  ],
  providers: [StreamDataService, TrendDataService],
  bootstrap: [AppComponent]
})
export class AppModule { }
