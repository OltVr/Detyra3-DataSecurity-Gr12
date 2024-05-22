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
8. **Terminimi i komunikimit**: Pas përfundimit të komunikimit, lidhja mund të terminohet.

### DHClient.java
1. **Gjenerimi i çelësave Diffie-Hellman**: Klienti gjeneron një çift çelësash DH (private dhe public).
2. **Lidhja me serverin**: Klienti lidhët me serverin dhe fillon komunikimin.
3. **Shkëmbimi i çelësave**: Klienti merr çelësin publik DH të serverit dhe dërgon çelësin e tij publik DH tek serveri.
4. **Gjenerimi i çelësit të përbashkët**: Klienti gjeneron një çelës të përbashkët simetrik (AES) bazuar në çelësin DH të serverit.
5. **Verifikimi i nënshkrimit**: Klienti verifikon nënshkrimin e mesazhit të mirëseardhjes së serverit duke përdorur çelësin publik RSA të serverit.
6. **Komunikimi i sigurt**: Pas vendosjes së çelësit të përbashkët dhe verifikimit të nënshkrimit, klienti dhe serveri mund të shkëmbejnë mesazhe të koduara.
7. **Terminimi i komunikimit**: Pas përfundimit të komunikimit, lidhja mund të terminohet.

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
1. **Inicializimi i lidhjes**:
[ServerStart](https://github.com/OltVr/Detyra3-DataSecurity-Gr12/assets/106235563/1e885781-b59f-482c-9dbe-78024d037943)

2. **Dërgimi i mesazheve dhe marrja e mesazheve**:
![ServerMessage](https://github.com/OltVr/Detyra3-DataSecurity-Gr12/assets/106235563/b287388e-ea99-4e63-9468-bb948b09824e)
![ServerRecieve](https://github.com/OltVr/Detyra3-DataSecurity-Gr12/assets/106235563/d7673a39-5c78-40c3-9695-588c027c703b)

3. **Terminimi i komunikimit**:
![ServerTerminate](https://github.com/OltVr/Detyra3-DataSecurity-Gr12/assets/106235563/d46bbe03-103b-441a-8c13-1f8219e48337)

### Klienti:
1. **Inicializimi i lidhjes**:
![ClientStart](https://github.com/OltVr/Detyra3-DataSecurity-Gr12/assets/106235563/ee8b1a3a-ade2-4377-bb6e-8a4daa2f77c2)

2. **Dërgimi i mesazheve dhe marrja e mesazheve**:
![ClientMessage](https://github.com/OltVr/Detyra3-DataSecurity-Gr12/assets/106235563/30444aa2-29ca-44b9-8422-4378c1aa18ac)
![ClientRecieve](https://github.com/OltVr/Detyra3-DataSecurity-Gr12/assets/106235563/b90521db-e0a7-434a-ab02-3f16a4d94759)

3. **Terminimi i komunikimit**:
![ClientTerminate](https://github.com/OltVr/Detyra3-DataSecurity-Gr12/assets/106235563/a710febf-d6cc-4f76-bb1a-a60a4b7d8901)
