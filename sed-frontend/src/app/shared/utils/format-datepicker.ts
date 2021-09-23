import { Injectable } from "@angular/core";
@Injectable({
    providedIn: 'root'
})
export class FormatDatepickers {

    formatDate(date: any) {
        let dateParts = date.split("/");
        let dateObject = new Date(+dateParts[2], date[1] - 1, +dateParts[0]);
        let today: any = new Date(dateObject);
        let dd: any = today.getDate();
        let mm = today.getMonth() + 1;
        let yyyy = today.getFullYear();
        if (dd < 10) {
            dd = '0' + dd;
        }

        if (mm < 10) {
            mm = '0' + mm;
        }
        return yyyy + '-' + mm + '-' + dd;
    }
}