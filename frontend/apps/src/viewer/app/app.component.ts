import { Component, HostListener } from '@angular/core';

@Component({
  selector: 'nxt-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {

  isFullscreen = false;

  @HostListener('document:webkitfullscreenchange')
  onFullscreenChange() {
    this.isFullscreen = !!document.webkitFullscreenElement;
  }

  toggleFullscreen() {
    const target = document.body;
    if (!document.webkitFullscreenElement) {
      target.webkitRequestFullScreen();
    } else {
      document.webkitExitFullscreen();
    }
  }
}
