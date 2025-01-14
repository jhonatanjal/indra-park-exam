import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FormSaidaComponent } from './form-saida.component';

describe('FormSaidaComponent', () => {
  let component: FormSaidaComponent;
  let fixture: ComponentFixture<FormSaidaComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FormSaidaComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FormSaidaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
