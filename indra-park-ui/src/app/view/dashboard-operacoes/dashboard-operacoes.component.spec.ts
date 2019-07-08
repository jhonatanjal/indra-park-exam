import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DashboardOperacoesComponent } from './dashboard-operacoes.component';

describe('DashboardOperacoesComponent', () => {
  let component: DashboardOperacoesComponent;
  let fixture: ComponentFixture<DashboardOperacoesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DashboardOperacoesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DashboardOperacoesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
