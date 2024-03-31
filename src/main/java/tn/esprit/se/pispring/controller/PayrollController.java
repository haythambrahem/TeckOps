package tn.esprit.se.pispring.controller;

import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.se.pispring.Service.ContributionService;
import tn.esprit.se.pispring.Service.PayrollService;
import tn.esprit.se.pispring.Service.PrimeService;
import tn.esprit.se.pispring.entities.Payroll;
import tn.esprit.se.pispring.entities.Prime;
import tn.esprit.se.pispring.utils.GeneratePdfPayroll;

import java.io.ByteArrayInputStream;
import java.util.List;


@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
@AllArgsConstructor
@RequestMapping("/Payroll")
public class PayrollController  {
    PayrollService payrollService;
    PrimeService primeService;
    ContributionService contributionService;

    @GetMapping("/retrieve-all-payrolls")
    public List<Payroll> getPayrolls() {
        List<Payroll> listPayrolls = payrollService.retrieveAllPayrolls();
        return listPayrolls;
    }
    @PostMapping("/add-payroll")
    public Payroll addPayroll(@RequestBody Payroll payroll) {
        return payrollService.addPayroll(payroll);
    }

    @PutMapping("/update-payroll/{idpayroll}")
    public Payroll updatePayroll(@RequestBody Payroll payroll, @PathVariable Long idpayroll) {
        return payrollService.updatePayroll(payroll,idpayroll);
    }

    @PutMapping("/affect-payroll/{userId}")
    public Payroll affectPayrollUser(@RequestBody Payroll payroll, @PathVariable Long userId) {
        return payrollService.affectPayrollUser(payroll,userId);
    }

    @RequestMapping(value = "/pdfpayroll/{idpayroll}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> payrollReport(@PathVariable Long idpayroll) {

        var payroll = (Payroll) payrollService.retrievePayroll(idpayroll);
        var primes = primeService.getListPrimeByUserAndMonth(payroll.getUser(),payroll.getMonth(), payroll.getYear());
        var contributions = contributionService.getListContributionByUserAndMonth(payroll.getUser(),payroll.getMonth(), payroll.getYear());
        System.out.println(payroll.toString());
        GeneratePdfPayroll generatePdfPayroll = new GeneratePdfPayroll();

        ByteArrayInputStream bis = generatePdfPayroll.payrollReport(payroll,primes, contributions);

        var headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=payroll.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }


    @GetMapping("/retrieve-payroll/{id}")
    public Payroll getPayroll(@PathVariable Long id) {return payrollService.retrievePayroll(id);}

    @DeleteMapping("/remove-payroll/{id}")
    public void removePayroll(@PathVariable Long id) {
        payrollService.removePayroll(id);
    }
}

