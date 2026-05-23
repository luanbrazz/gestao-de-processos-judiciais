import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { ProcessoService } from '../../../../core/services/processo.service';
import { Processo, StatusProcesso } from '../../../../core/models/processo.model';
import { StatusBadgeComponent } from '../../../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-processo-list',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule, StatusBadgeComponent],
  templateUrl: './processo-list.component.html'
})
export class ProcessoListComponent implements OnInit {

  processos: Processo[] = [];
  totalElements = 0;
  totalPages = 0;
  currentPage = 0;
  pageSize = 10;
  statusFiltro: StatusProcesso | '' = '';

  readonly statusOptions: StatusProcesso[] = ['ATIVO', 'SUSPENSO', 'ENCERRADO'];

  constructor(
    private processoService: ProcessoService,
    private spinner: NgxSpinnerService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.spinner.show();
    this.processoService.listar(
      this.currentPage,
      this.pageSize,
      this.statusFiltro || undefined
    ).subscribe({
      next: page => {
        this.processos = page.content;
        this.totalElements = page.totalElements;
        this.totalPages = page.totalPages;
        this.spinner.hide();
      },
      error: () => {
        this.toastr.error('Erro ao carregar processos');
        this.spinner.hide();
      }
    });
  }

  filtrar(): void {
    this.currentPage = 0;
    this.carregar();
  }

  irParaPagina(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.carregar();
    }
  }

  pages(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i);
  }
}
