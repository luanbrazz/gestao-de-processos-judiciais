import { Component } from '@angular/core';
import { RouterOutlet, RouterLink } from '@angular/router';
import { NgxSpinnerModule } from 'ngx-spinner';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, NgxSpinnerModule],
  templateUrl: './app.component.html',
})
export class AppComponent {}
