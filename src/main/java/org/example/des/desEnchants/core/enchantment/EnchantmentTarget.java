package org.example.des.desEnchants.core.enchantment;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum EnchantmentTarget {
    // Armor pieces
    HELMET {
        @Override
        public boolean isValidItem(ItemStack item) {
            Material type = item.getType();
            return type.name().endsWith("_HELMET") || type == Material.TURTLE_HELMET;
        }
    },
    CHESTPLATE {
        @Override
        public boolean isValidItem(ItemStack item) {
            Material type = item.getType();
            return type.name().endsWith("_CHESTPLATE") || type == Material.ELYTRA;
        }
    },
    LEGGINGS {
        @Override
        public boolean isValidItem(ItemStack item) {
            return item.getType().name().endsWith("_LEGGINGS");
        }
    },
    BOOTS {
        @Override
        public boolean isValidItem(ItemStack item) {
            return item.getType().name().endsWith("_BOOTS");
        }
    },
    ALL_ARMOR {
        @Override
        public boolean isValidItem(ItemStack item) {
            return HELMET.isValidItem(item) || CHESTPLATE.isValidItem(item) ||
                    LEGGINGS.isValidItem(item) || BOOTS.isValidItem(item);
        }
    },

    // Weapons
    SWORD {
        @Override
        public boolean isValidItem(ItemStack item) {
            return item.getType().name().endsWith("_SWORD");
        }
    },
    AXE {
        @Override
        public boolean isValidItem(ItemStack item) {
            return item.getType().name().endsWith("_AXE");
        }
    },
    BOW {
        @Override
        public boolean isValidItem(ItemStack item) {
            Material type = item.getType();
            return type == Material.BOW || type == Material.CROSSBOW;
        }
    },
    TRIDENT {
        @Override
        public boolean isValidItem(ItemStack item) {
            return item.getType() == Material.TRIDENT;
        }
    },
    ALL_WEAPONS {
        @Override
        public boolean isValidItem(ItemStack item) {
            return SWORD.isValidItem(item) || AXE.isValidItem(item) ||
                    BOW.isValidItem(item) || TRIDENT.isValidItem(item);
        }
    },

    // Tools
    PICKAXE {
        @Override
        public boolean isValidItem(ItemStack item) {
            return item.getType().name().endsWith("_PICKAXE");
        }
    },
    SHOVEL {
        @Override
        public boolean isValidItem(ItemStack item) {
            return item.getType().name().endsWith("_SHOVEL");
        }
    },
    HOE {
        @Override
        public boolean isValidItem(ItemStack item) {
            return item.getType().name().endsWith("_HOE");
        }
    },
    ALL_TOOLS {
        @Override
        public boolean isValidItem(ItemStack item) {
            return PICKAXE.isValidItem(item) || AXE.isValidItem(item) ||
                    SHOVEL.isValidItem(item) || HOE.isValidItem(item);
        }
    };

    public abstract boolean isValidItem(ItemStack item);

    public String getDisplayName() {
        return switch (this) {
            case HELMET -> "Helmets";
            case CHESTPLATE -> "Chestplates";
            case LEGGINGS -> "Leggings";
            case BOOTS -> "Boots";
            case ALL_ARMOR -> "All Armor";
            case SWORD -> "Swords";
            case AXE -> "Axes";
            case BOW -> "Bows & Crossbows";
            case TRIDENT -> "Tridents";
            case ALL_WEAPONS -> "All Weapons";
            case PICKAXE -> "Pickaxes";
            case SHOVEL -> "Shovels";
            case HOE -> "Hoes";
            case ALL_TOOLS -> "All Tools";
        };
    }
}