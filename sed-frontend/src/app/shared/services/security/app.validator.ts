import { FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';

export const validarQueSeanIguales: ValidatorFn = (control: FormGroup): ValidationErrors | null => {
  const password = control.get('newPass');
  const confirmarPassword = control.get('confirmPass');

  return password.value === confirmarPassword.value ? null : { 'noSonIguales': true };
};
