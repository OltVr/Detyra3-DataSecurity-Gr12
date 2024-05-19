# Diffie-Hellman and Digital Signatures Console Application

## Përmbledhje
Ky është një program për komunikim të sigurt klient-server që përdor algoritmin Diffie-Hellman për të vendosur një çelës të përbashkët dhe nënshkrimet dixhitale për të siguruar integritetin dhe autenticitetin e mesazheve të shkëmbyera.

## Pjesët e Programit

### DHServer.java
1. **Gjenerimi i çelësave Diffie-Hellman**: Serveri gjeneron një çift çelësash DH (private dhe public).
2. **Nënshkrimi dixhital**: Serveri gjeneron një çift çelësash RSA për nënshkrimin dhe verifikimin e mesazheve.
3. **Lidhja me klientin**: Serveri pret lidhjet nga klientët dhe komunikon me ta.
4. **Shkëmbimi i çelësave**: Serveri dërgon çelësin publik DH tek klienti dhe merr çelësin publik DH të klientit.
5. **Gjenerimi i çelësit të përbashkët**: Serveri gjeneron një çelës të përbashkët simetrik (AES) bazuar në çelësin DH të klientit.
6. **Nënshkrimi i mesazheve**: Serveri nënshkruan një mesazh mirëseardhjeje dhe e dërgon së bashku me çelësin publik RSA të tij.
7. **Komunikimi i sigurt**: Pas vendosjes së çelësit të përbashkët dhe verifikimit të nënshkrimit, serveri dhe klienti mund të shkëmbejnë mesazhe të koduara.

### DHClient.java
1. **Gjenerimi i çelësave Diffie-Hellman**: Klienti gjeneron një çift çelësash DH (private dhe public).
2. **Lidhja me serverin**: Klienti lidhët me serverin dhe fillon komunikimin.
3. **Shkëmbimi i çelësave**: Klienti merr çelësin publik DH të serverit dhe dërgon çelësin e tij publik DH tek serveri.
4. **Gjenerimi i çelësit të përbashkët**: Klienti gjeneron një çelës të përbashkët simetrik (AES) bazuar në çelësin DH të serverit.
5. **Verifikimi i nënshkrimit**: Klienti verifikon nënshkrimin e mesazhit të mirëseardhjes së serverit duke përdorur çelësin publik RSA të serverit.
6. **Komunikimi i sigurt**: Pas vendosjes së çelësit të përbashkët dhe verifikimit të nënshkrimit, klienti dhe serveri mund të shkëmbejnë mesazhe të koduara.

## Udhëzime për Ekzekutimin e Programit
1. **Kompilimi i klasave të serverit dhe klientit**:
    ```sh
    javac DHServer.java
    javac DHClient.java
    ```

 2. **Ekzekutimi i serverit**:
    ```sh
    java DHServer
    ```

3. **Ekzekutimi i klientit**:
    ```sh
    java DHClient
    ```

 ## Shembull i Rezultateve të Ekzekutimit

### Serveri:

### Klienti:
