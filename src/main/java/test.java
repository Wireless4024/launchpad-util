/*
// interface พิมพ์เขียวหรือแม่แบบของ สิ่งมีชีวิต
interface Organism {
    void breath(); // ความสามารถในการหายใจ
    void eat(); // ความสามารถในการกิน
}

// interface พิมพ์เขียวหรือแม่แบบของ สิ่งมีชีวิตที่มีขา
interface OrganismWithLegs extends Organism {
    int legCount(); // จำนวนขา
    void walk(); // ความสามารถในการเดิน
    void run(); // ความสามารถในการวิ่ง
    void sit(); // ความสามารถในการนั่ง
    void stand(); // ความสามารถในการยืน
}

// abstract class ทำหน้าที่ implement ความสามารถที่ subclass จะต้องนำไปทำซ้ำๆ
// ซึ่งมนุษย์ สืบทอดความสามารถมาจาก สิ่งมีชีวตที่มีชา
abstract class Person implements OrganismWithLegs {
    String name; // ชื่อ
    String role; // หน้าที่
    int age; // อายุ
    public int legCount() { return 2; } // จำนวนขาของมนุษย์คือ 2
    public void walk() { } // ความสามารถในการเดิน
    public void run() { } // ความสามารถในการวิ่ง
    public void sit() { } // ความสามารถในการนั่ง
    public void stand() { } // ความสามารถในการยืน
    public void breath() { } // ความสามารถในการหายใจ
    public void eat() { } // ความสามารถในการกิน
    abstract void doWork(); // วิธีการทำงานของมนุษย์แต่ละคน ซึ่งต้องให้ subclass นำไป implement
}

// นักเรียนสืบทอดความสามารถมาจาก มนุษย์
class Student extends Person {
    public Student() { this.role = "Student"; } // มีหน้าที่เป็น นักเรียน
    public void learn() { } // ความสามารถในการเรียนรู้
    void doWork() { learn(); } // การทำงาน ซึ่งสืบทอดมาจาก abstract
}

// อาจารย์ ที่สืบทอดความสามารถมาจากนักเรียน และมีหน้าที่เป็นอาจารย์
// อาจารย์สามารถใช้ความสามารถในการเรียนรู้จากคลาสนักเรียนได้
class Teacher extends Student {
    public Teacher() { this.role = "Teacher"; } // ให้หน้าที่เป็นอาจารย์
    public void teach() { } // ความสามารถในการสอน
    void doWork() { teach(); } // การทำงาน ซึ่งสืบทอดมาจาก abstract
}

public class test{}
*/

class test{

}