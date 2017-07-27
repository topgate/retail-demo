import { enableProdMode } from '@angular/core';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';

import 'rxjs/add/observable/of';
import 'rxjs/add/operator/scan';
import 'rxjs/add/operator/filter';
import 'rxjs/add/operator/debounceTime';
import 'rxjs/add/operator/delay';
import 'rxjs/add/operator/switchMap';
import 'rxjs/add/operator/concatAll';

import { AppModule } from './app/app.module';
import { environment } from './environments/environment';

enableProdMode();
if (environment.production) {
}

platformBrowserDynamic().bootstrapModule(AppModule);
