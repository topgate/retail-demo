import { Injectable, NgZone, OnDestroy } from '@angular/core';
import { Subject } from 'rxjs/Subject';
import { Observable } from 'rxjs/Observable';
import { AngularFireDatabase } from 'angularfire2/database';
import { environment } from './../../environments/environment';

@Injectable()
export class TrendDataService implements OnDestroy {

  private queue$ = new Subject<any>();

  constructor(private db: AngularFireDatabase, private ngZone: NgZone) {
    this.ngZone.runOutsideAngular(() => {
      this.db.database.ref(environment.database.trend)
        .on('value', snapshot => {
          const children = snapshot.val();
          this.ngZone.run(() => {
            this.queue$.next(children);
          });
        });
    });
  }

  get data$(): Observable<any> {
    return this.queue$.asObservable().filter(data => !!data);
  }

  ngOnDestroy() {
    this.db.database.ref(environment.database.trend).off('value');
  }
}
