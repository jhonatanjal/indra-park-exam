import { TestBed } from '@angular/core/testing';

import { OperacoesService } from './operacoes.service';

describe('OperacoesService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: OperacoesService = TestBed.get(OperacoesService);
    expect(service).toBeTruthy();
  });
});
